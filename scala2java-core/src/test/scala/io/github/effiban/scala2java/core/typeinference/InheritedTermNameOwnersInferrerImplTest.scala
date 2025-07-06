package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher.eqQualificationContext
import io.github.effiban.scala2java.core.qualifiers.QualificationContext
import io.github.effiban.scala2java.core.reflection.ScalaReflectionLookup
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any

import scala.collection.immutable.ListMap
import scala.meta.{Type, XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class InheritedTermNameOwnersInferrerImplTest extends UnitTestSuite {

  private val enclosingTemplateAncestorsInferrer = mock[EnclosingTemplateAncestorsInferrer]
  private val templateAncestorsInferrer = mock[TemplateAncestorsInferrer]
  private val scalaReflectionLookup = mock[ScalaReflectionLookup]

  private val inheritedTermNameOwnersInferrer = new InheritedTermNameOwnersInferrerImpl(
    enclosingTemplateAncestorsInferrer,
    templateAncestorsInferrer,
    scalaReflectionLookup
  )

  test("infer() when the template has no ancestors, should return empty") {
    val templ = template"X"
    val termName = q"x"

    val context = QualificationContext()

    doReturn(Nil).when(templateAncestorsInferrer).infer(eqTree(templ), eqQualificationContext(context))

    inheritedTermNameOwnersInferrer.infer(termName, templ, context) shouldBe empty
  }

  test("infer() when the template has ancestors, but the Term.Name is not a member of any parent - should return empty") {
    val templ = template"X"
    val termName = q"x"

    val context = QualificationContext(qualifiedTypeMap = Map(t"Parent1" -> t"mypkg.Parent1"))

    doReturn(
      List(
        t"mypkg.Parent1",
        t"mypkg.Grandparent1"
      )
    ).when(templateAncestorsInferrer).infer(eqTree(templ), eqQualificationContext(context))

    when(scalaReflectionLookup.isTermMemberOf(any[Type.Ref], eqTree(termName))).thenReturn(false)

    inheritedTermNameOwnersInferrer.infer(termName, templ, context) shouldBe empty
  }

  test("infer() when the template has ancestors, and the Term.Name is a member of some, should return a corresponding Map") {
    val templ = template"X"
    val termName = q"x"

    val context = QualificationContext(qualifiedTypeMap = Map(t"Parent1" -> t"mypkg.Parent1"))

    doReturn(
      List(
        t"mypkg.Parent1",
        t"mypkg.Grandparent1"
      )
    ).when(templateAncestorsInferrer).infer(eqTree(templ), eqQualificationContext(context))

    when(scalaReflectionLookup.isTermMemberOf(any[Type.Ref], eqTree(termName))).thenReturn(true)

    inheritedTermNameOwnersInferrer.infer(termName, templ, context).structure shouldBe
      List(
        t"mypkg.Parent1",
        t"mypkg.Grandparent1"
      ).structure
  }

  test("inferAll() when Term.Name has one enclosing member without ancestors, should return empty") {
    val termName = q"x"

    val context = QualificationContext()

    doReturn(ListMap.empty).when(enclosingTemplateAncestorsInferrer).infer(eqTree(termName), eqQualificationContext(context))

    inheritedTermNameOwnersInferrer.inferAll(termName, context) shouldBe empty
  }

  test("inferAll() when Term.Name has one enclosing member with ancestors, but it's not a member of any parent, should return empty") {
    val templ = template"X"
    val termName = q"x"

    val context = QualificationContext(qualifiedTypeMap = Map(t"Parent1" -> t"mypkg.Parent1"))

    doReturn(ListMap(templ ->
      List(
        t"mypkg.Parent1",
        t"mypkg.Grandparent1"
      )
    )).when(enclosingTemplateAncestorsInferrer).infer(eqTree(termName), eqQualificationContext(context))

    when(scalaReflectionLookup.isTermMemberOf(any[Type.Ref], eqTree(termName))).thenReturn(false)

    inheritedTermNameOwnersInferrer.inferAll(termName, context) shouldBe empty
  }

  test("inferAll() when Term.Name has one enclosing member with ancestors, and it's a member of some, should return a corresponding Map") {
    val templ = template"X"
    val termName = q"x"

    val context = QualificationContext(qualifiedTypeMap = Map(t"Parent1" -> t"mypkg.Parent1"))

    doReturn(ListMap(templ ->
      List(
        t"mypkg.Parent1",
        t"mypkg.Grandparent1"
      )
    )).when(enclosingTemplateAncestorsInferrer).infer(eqTree(termName), eqQualificationContext(context))

    when(scalaReflectionLookup.isTermMemberOf(any[Type.Ref], eqTree(termName))).thenReturn(true)

    val resultMap = inheritedTermNameOwnersInferrer.inferAll(termName, context)
    resultMap.size shouldBe 1
    val (resultTemplate, resultParents) = resultMap.head
    resultTemplate.structure shouldBe templ.structure
    resultParents.structure shouldBe List(
      t"mypkg.Parent1",
      t"mypkg.Grandparent1"
    ).structure
  }

  test("inferAll() when Term.Name has two enclosing members with ancestors, and it's a member of one, should return a corresponding Map") {
    val templ1 = template"T1"
    val templ2 = template"T2"
    val termName = q"x"

    val context = QualificationContext(qualifiedTypeMap = Map(
      t"Parent1" -> t"mypkg.Parent1",
      t"Parent2" -> t"mypkg.Parent2"
    ))

    doReturn(ListMap(
      templ1 -> List(
        t"mypkg.Parent1",
        t"mypkg.Grandparent1",
      ),
      templ2 -> List(
        t"mypkg.Parent2",
        t"mypkg.Grandparent2"
      ),
    )).when(enclosingTemplateAncestorsInferrer).infer(eqTree(termName), eqQualificationContext(context))

    when(scalaReflectionLookup.isTermMemberOf(any[Type.Ref], eqTree(termName))).thenAnswer((ancestor: Type.Ref) =>{
      ancestor match {
        case t"mypkg.Parent1" |
             t"mypkg.Grandparent1" => true
        case _ => false
      }
    })

    val resultMap = inheritedTermNameOwnersInferrer.inferAll(termName, context)
    resultMap.size shouldBe 1
    val (resultMember, resultParents) = resultMap.head
    resultMember.structure shouldBe templ1.structure
    resultParents.structure shouldBe List(
      t"mypkg.Parent1",
      t"mypkg.Grandparent1"
    ).structure
  }

  test("inferAll() when Term.Name has two enclosing members with ancestors, and it's a member of both, should return a corresponding Map") {
    val templ1 = template"T1"
    val templ2 = template"T2"
    val termName = q"x"

    val context = QualificationContext(qualifiedTypeMap = Map(
      t"Parent1" -> t"mypkg.Parent1",
      t"Parent2" -> t"mypkg.Parent2"
    ))

    doReturn(ListMap(
      templ1 -> List(
        t"mypkg.Parent1",
        t"mypkg.Grandparent1",
      ),
      templ2 -> List(
        t"mypkg.Parent2",
        t"mypkg.Grandparent2"
      )
    )).when(enclosingTemplateAncestorsInferrer).infer(eqTree(termName), eqQualificationContext(context))

    when(scalaReflectionLookup.isTermMemberOf(any[Type.Ref], eqTree(termName))).thenReturn(true)

    val resultMap = inheritedTermNameOwnersInferrer.inferAll(termName, context)
    resultMap.size shouldBe 2

    val inferredChild1Parents = TreeKeyedMap(resultMap, templ1)
    inferredChild1Parents.structure shouldBe List(
      t"mypkg.Parent1",
      t"mypkg.Grandparent1"
    ).structure

    val inferredChild2Parents = TreeKeyedMap(resultMap, templ2)
    inferredChild2Parents.structure shouldBe List(
      t"mypkg.Parent2",
      t"mypkg.Grandparent2"
    ).structure
  }
}

