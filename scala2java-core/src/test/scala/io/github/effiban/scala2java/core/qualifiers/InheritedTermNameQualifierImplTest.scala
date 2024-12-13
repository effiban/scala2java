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

  private val inheritedTermNameOwnersInferrer = mock[InheritedTermNameOwnersInferrer]
  private val fileScopeNonInheritedTermNameBinder = mock[FileScopeNonInheritedTermNameBinder]

  private val inheritedTermNameQualifier = new InheritedTermNameQualifierImpl(
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

    doReturn(ListMap((templA, List(t"qualB.B")))).when(inheritedTermNameOwnersInferrer).inferAll(eqTree(x), eqQualificationContext(context))
    when(fileScopeNonInheritedTermNameBinder.bind(eqTree(x))).thenReturn(None)

    inheritedTermNameQualifier.qualify(x, context).value.structure shouldBe q"A.super[B].x".structure
  }

  test("qualify for term that is inherited from two parents of the same template - " +
    "should return a corresponding 'super' term for first parent") {
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

    doReturn(ListMap((templA, List(t"qualB.B", t"qualC.C"))))
      .when(inheritedTermNameOwnersInferrer).inferAll(eqTree(x), eqQualificationContext(context))
    when(fileScopeNonInheritedTermNameBinder.bind(eqTree(x))).thenReturn(None)

    inheritedTermNameQualifier.qualify(x, context).value.structure shouldBe q"A.super[B].x".structure
  }

  test("qualify for term that is inherited from a parent of an inner class - should return a corresponding 'super' term") {
    val clsA =
      q"""
      class A {
        class B extends C {
          def foo(): Int = x
        }
      }
      """

    val clsB = clsA.templ.stats.find { case cls: Defn.Class => cls.name.value == "B"}.get.asInstanceOf[Defn.Class]
    val templB = clsB.templ
    val foo = templB.stats.collectFirst {case defnDef: Defn.Def => defnDef}.get
    val x = foo.body.asInstanceOf[Term.Name]

    val context = QualificationContext(qualifiedTypeMap = ListMap(
      t"C" -> t"qualC.C"
    ))

    doReturn(ListMap((templB, List(t"qualC.C"))))
      .when(inheritedTermNameOwnersInferrer).inferAll(eqTree(x), eqQualificationContext(context))
    when(fileScopeNonInheritedTermNameBinder.bind(eqTree(x))).thenReturn(None)

    inheritedTermNameQualifier.qualify(x, context).value.structure shouldBe q"B.super[C].x".structure
  }

  test("qualify for term that is inherited from a parent of an outer class - should return a corresponding 'super' term") {
    val clsA =
      q"""
      class A extends B {
        class C {
          def foo(): Int = x
        }
      }
      """

    val templA = clsA.templ
    val clsC = clsA.templ.stats.find { case cls: Defn.Class => cls.name.value == "C"}.get.asInstanceOf[Defn.Class]
    val templC = clsC.templ
    val foo = templC.stats.collectFirst {case defnDef: Defn.Def => defnDef}.get
    val x = foo.body.asInstanceOf[Term.Name]

    val context = QualificationContext(qualifiedTypeMap = ListMap(
      t"B" -> t"qualB.B"
    ))

    doReturn(ListMap((templA, List(t"qualB.B"))))
      .when(inheritedTermNameOwnersInferrer).inferAll(eqTree(x), eqQualificationContext(context))
    when(fileScopeNonInheritedTermNameBinder.bind(eqTree(x))).thenReturn(None)

    inheritedTermNameQualifier.qualify(x, context).value.structure shouldBe q"A.super[B].x".structure
  }

  test("qualify for term that is inherited from parents of both an outer and an inner class -" +
    " should return a corresponding 'super' term for the inner class parent") {
    val clsA =
      q"""
      class A extends B {
        class C extends D {
          def foo(): Int = x
        }
      }
      """

    val templA = clsA.templ
    val clsC = clsA.templ.stats.find { case cls: Defn.Class => cls.name.value == "C"}.get.asInstanceOf[Defn.Class]
    val templC = clsC.templ
    val foo = templC.stats.collectFirst {case defnDef: Defn.Def => defnDef}.get
    val x = foo.body.asInstanceOf[Term.Name]

    val context = QualificationContext(qualifiedTypeMap = ListMap(
      t"B" -> t"qualB.B",
      t"D" -> t"qualD.D"
    ))

    // Inner class entry will be returned first by the inferrer
    doReturn(ListMap(
      (templC, List(t"qualD.D")),
      (templA, List(t"qualB.B")),
    )).when(inheritedTermNameOwnersInferrer).inferAll(eqTree(x), eqQualificationContext(context))
    when(fileScopeNonInheritedTermNameBinder.bind(eqTree(x))).thenReturn(None)

    inheritedTermNameQualifier.qualify(x, context).value.structure shouldBe q"C.super[D].x".structure
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

    doReturn(ListMap((templA, List(t"qualB.B")))).when(inheritedTermNameOwnersInferrer).inferAll(eqTree(x), eqQualificationContext(context))
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

    doReturn(ListMap.empty).when(inheritedTermNameOwnersInferrer).inferAll(eqTree(x), eqQualificationContext(context))
    when(fileScopeNonInheritedTermNameBinder.bind(eqTree(x))).thenReturn(None)

    inheritedTermNameQualifier.qualify(x, context) shouldBe None
  }

  test("qualify when has no enclosing template and no inherited owners should return None") {
    val termName = q"x"

    val context = QualificationContext()

    doReturn(ListMap.empty).when(inheritedTermNameOwnersInferrer).inferAll(eqTree(termName), eqQualificationContext(context))

    inheritedTermNameQualifier.qualify(termName, context) shouldBe None
  }
}
