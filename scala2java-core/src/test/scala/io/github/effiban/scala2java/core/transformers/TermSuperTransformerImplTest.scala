package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TermSupers
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.typeinference.InnermostEnclosingTemplateInferrer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo
import org.mockito.Mockito.verifyNoInteractions

import scala.meta.{Term, XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TermSuperTransformerImplTest extends UnitTestSuite {

  private val innermostEnclosingTemplateInferrer = mock[InnermostEnclosingTemplateInferrer]
  private val treeTransformer = mock[TreeTransformer]

  private val termSuperTransformer = new TermSuperTransformerImpl(innermostEnclosingTemplateInferrer, treeTransformer)

  test("transform when has a superp and an enclosing template, when first parent matches") {
    val template =
      template"""
      a.A with b.B { self: c.C =>
        val x = super[A].y
      }
      """

    val theSuper = template.collect { case aSuper: Term.Super => aSuper}.head

    when(innermostEnclosingTemplateInferrer.infer(eqTree(theSuper), eqTo(None))).thenReturn(Some(template))
    when(treeTransformer.transform(eqTree(t"a.A"))).thenReturn(t"a.AA")

    termSuperTransformer.transform(theSuper).structure shouldBe q"super[AA]".structure
  }

  test("transform when has a superp and an enclosing template, when second parent matches") {
    val template =
      template"""
      a.A with b.B { self: c.C =>
        val x = super[B].y
      }
      """

    val theSuper = template.collect { case aSuper: Term.Super => aSuper}.head

    when(innermostEnclosingTemplateInferrer.infer(eqTree(theSuper), eqTo(None))).thenReturn(Some(template))
    when(treeTransformer.transform(eqTree(t"b.B"))).thenReturn(t"b.BB")

    termSuperTransformer.transform(theSuper).structure shouldBe q"super[BB]".structure
  }

  test("transform when has a superp and an enclosing template, and 'self' matches") {
    val template =
      template"""
      a.A with b.B { self: c.C =>
        val x = super[C].y
      }
      """

    val theSuper = template.collect { case aSuper: Term.Super => aSuper}.head

    when(innermostEnclosingTemplateInferrer.infer(eqTree(theSuper), eqTo(None))).thenReturn(Some(template))
    when(treeTransformer.transform(eqTree(t"c.C"))).thenReturn(t"c.CC")

    termSuperTransformer.transform(theSuper).structure shouldBe q"super[CC]".structure
  }

  test("transform when has a superp but no enclosing template, should return unchanged") {
    val theSuper = q"super[A]"

    when(innermostEnclosingTemplateInferrer.infer(eqTree(theSuper), eqTo(None))).thenReturn(None)

    termSuperTransformer.transform(theSuper).structure shouldBe q"super[A]".structure

    verifyNoInteractions(treeTransformer)
  }

  test("transform when has no superp and has an enclosing template, should return unchanged") {
    val template =
      template"""
      a.A with b.B { self: c.C =>
        val x = super.y
      }
      """

    val theSuper = template.collect { case aSuper: Term.Super => aSuper}.head

    termSuperTransformer.transform(theSuper).structure shouldBe theSuper.structure

    verifyNoInteractions(innermostEnclosingTemplateInferrer, treeTransformer)
  }

  test("transform when has no superp and no enclosing template, should return unchanged") {
    val theSuper = TermSupers.Empty

    when(innermostEnclosingTemplateInferrer.infer(eqTree(TermSupers.Empty), eqTo(None))).thenReturn(None)

    termSuperTransformer.transform(TermSupers.Empty).structure shouldBe TermSupers.Empty.structure

    verifyNoInteractions(treeTransformer)
  }

}
