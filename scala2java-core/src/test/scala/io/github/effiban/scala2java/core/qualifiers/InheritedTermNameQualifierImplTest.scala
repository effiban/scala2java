package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.{InheritedTermNameOwnersInferrer, InnermostEnclosingTemplateInferrer}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.collection.MapView
import scala.meta.{Defn, Name, Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class InheritedTermNameQualifierImplTest extends UnitTestSuite {

  private val innermostEnclosingTemplateInferrer = mock[InnermostEnclosingTemplateInferrer]
  private val inheritedTermNameOwnersInferrer = mock[InheritedTermNameOwnersInferrer]

  private val inheritedTermNameQualifier = new InheritedTermNameQualifierImpl(
    innermostEnclosingTemplateInferrer,
    inheritedTermNameOwnersInferrer
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

    when(innermostEnclosingTemplateInferrer.infer(eqTree(x), eqTo(None))).thenReturn(Some(templA))
    doReturn(MapView((templA, List(t"B")))).when(inheritedTermNameOwnersInferrer).infer(eqTree(x))

    inheritedTermNameQualifier.qualify(x).value.structure shouldBe
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

    when(innermostEnclosingTemplateInferrer.infer(eqTree(x), eqTo(None))).thenReturn(Some(templA))
    doReturn(MapView((templA, List(t"B", t"C")))).when(inheritedTermNameOwnersInferrer).infer(eqTree(x))

    inheritedTermNameQualifier.qualify(x).value.structure shouldBe
      Term.Select(Term.Super(t"A", Name.Indeterminate("B")), q"x").structure
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

    when(innermostEnclosingTemplateInferrer.infer(eqTree(x), eqTo(None))).thenReturn(Some(templA))
    doReturn(MapView.empty).when(inheritedTermNameOwnersInferrer).infer(eqTree(x))

    inheritedTermNameQualifier.qualify(x) shouldBe None
  }

  test("qualify when has no enclosing template should return None") {
    val termName = q"x"
    when(innermostEnclosingTemplateInferrer.infer(eqTree(termName), eqTo(None))).thenReturn(None)

    inheritedTermNameQualifier.qualify(termName) shouldBe None
  }
}
