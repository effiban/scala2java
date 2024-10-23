package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.binders.FileScopeNonInheritedTermNameBinder
import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher.eqQualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.{InheritedTermNameOwnersInferrer, InnermostEnclosingTemplateInferrer}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.collection.immutable.ListMap
import scala.meta.{Defn, Name, Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class InheritedTermNameQualifierImplTest extends UnitTestSuite {

  private val innermostEnclosingTemplateInferrer = mock[InnermostEnclosingTemplateInferrer]
  private val inheritedTermNameOwnersInferrer = mock[InheritedTermNameOwnersInferrer]
  private val fileScopeNonInheritedTermNameBinder = mock[FileScopeNonInheritedTermNameBinder]

  private val inheritedTermNameQualifier = new InheritedTermNameQualifierImpl(
    innermostEnclosingTemplateInferrer,
    inheritedTermNameOwnersInferrer,
    fileScopeNonInheritedTermNameBinder
  )

  test("qualify for term that is inherited from one parent - should return a corresponding 'super' term") {
    val clsA =
      q"""
      class A extends B {
        def foo(): Int = x
      }
      """

    val templA = clsA.templ
    val foo = templA.stats.collectFirst {case defnDef: Defn.Def => defnDef}.get
    val x = foo.body.asInstanceOf[Term.Name]

    val context = QualificationContext(qualifiedTypeMap = ListMap(t"B" -> t"qualB.B"))

    when(innermostEnclosingTemplateInferrer.infer(eqTree(x), eqTo(None))).thenReturn(Some(templA))
    doReturn(ListMap((templA, List(t"qualB.B")))).when(inheritedTermNameOwnersInferrer).infer(eqTree(x), eqQualificationContext(context))
    when(fileScopeNonInheritedTermNameBinder.bind(eqTree(x))).thenReturn(None)

    inheritedTermNameQualifier.qualify(x, context).value.structure shouldBe
      Term.Select(Term.Super(t"A", Name.Indeterminate("B")), q"x").structure
  }

  test("qualify for term that is inherited from two parents - should return a corresponding 'super' term for first parent") {
    val clsA =
      q"""
      class A extends B with C {
        def foo(): Int = x
      }
      """

    val templA = clsA.templ
    val foo = templA.stats.collectFirst {case defnDef: Defn.Def => defnDef}.get
    val x = foo.body.asInstanceOf[Term.Name]

    val context = QualificationContext(qualifiedTypeMap = ListMap(
      t"B" -> t"qualB.B",
      t"C" -> t"qualC.C"
    ))

    when(innermostEnclosingTemplateInferrer.infer(eqTree(x), eqTo(None))).thenReturn(Some(templA))
    doReturn(ListMap((templA, List(t"qualB.B", t"qualC.C"))))
      .when(inheritedTermNameOwnersInferrer).infer(eqTree(x), eqQualificationContext(context))
    when(fileScopeNonInheritedTermNameBinder.bind(eqTree(x))).thenReturn(None)

    inheritedTermNameQualifier.qualify(x, context).value.structure shouldBe
      Term.Select(Term.Super(t"A", Name.Indeterminate("B")), q"x").structure
  }

  test("qualify for term that has an enclosing member, and is both inherited and defined locally - should return None") {
    val clsA =
      q"""
      class A extends B {
        val x = 3
        def foo(): Int = x
      }
      """

    val templA = clsA.templ
    val foo = templA.stats.collectFirst {case defnDef: Defn.Def => defnDef}.get
    val x = foo.body.asInstanceOf[Term.Name]

    val context = QualificationContext(qualifiedTypeMap = ListMap(t"B" -> t"qualB.B"))

    when(innermostEnclosingTemplateInferrer.infer(eqTree(x), eqTo(None))).thenReturn(Some(templA))
    doReturn(ListMap((templA, List(t"qualB.B")))).when(inheritedTermNameOwnersInferrer).infer(eqTree(x), eqQualificationContext(context))
    when(fileScopeNonInheritedTermNameBinder.bind(eqTree(x))).thenReturn(Some(templA))

    inheritedTermNameQualifier.qualify(x, context) shouldBe None
  }

  test("qualify for term that has an enclosing member, but is not inherited from any parent - should return None") {
    val clsA =
      q"""
      class A {
        def foo(): Int = x
      }
      """

    val templA = clsA.templ
    val foo = templA.stats.collectFirst {case defnDef: Defn.Def => defnDef}.get
    val x = foo.body.asInstanceOf[Term.Name]

    val context = QualificationContext()

    when(innermostEnclosingTemplateInferrer.infer(eqTree(x), eqTo(None))).thenReturn(Some(templA))
    doReturn(ListMap.empty).when(inheritedTermNameOwnersInferrer).infer(eqTree(x), eqQualificationContext(context))
    when(fileScopeNonInheritedTermNameBinder.bind(eqTree(x))).thenReturn(None)

    inheritedTermNameQualifier.qualify(x, context) shouldBe None
  }

  test("qualify when has no enclosing template and no inherited owners should return None") {
    val termName = q"x"

    val context = QualificationContext()

    when(innermostEnclosingTemplateInferrer.infer(eqTree(termName), eqTo(None))).thenReturn(None)
    doReturn(ListMap.empty).when(inheritedTermNameOwnersInferrer).infer(eqTree(termName), eqQualificationContext(context))

    inheritedTermNameQualifier.qualify(termName, context) shouldBe None
  }
}
