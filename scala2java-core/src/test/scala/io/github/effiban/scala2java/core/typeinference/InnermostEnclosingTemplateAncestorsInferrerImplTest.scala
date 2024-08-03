package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.collectors.TemplateAncestorsCollector
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.{Template, XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class InnermostEnclosingTemplateAncestorsInferrerImplTest extends UnitTestSuite {

  private val Term = q"x"
  private val EnclosingMemberName = "aaa"

  private val innermostEnclosingTemplateInferrer = mock[InnermostEnclosingTemplateInferrer]
  private val templateAncestorsCollector = mock[TemplateAncestorsCollector]
  
  private val innermostEnclosingTemplateAncestorsInferrer = new InnermostEnclosingTemplateAncestorsInferrerImpl(
    innermostEnclosingTemplateInferrer,
    templateAncestorsCollector
  )

  test("infer without name, when has no enclosing templates - should return empty") {
    when(innermostEnclosingTemplateInferrer.infer(eqTree(Term), eqTo(None))).thenReturn(None)

    innermostEnclosingTemplateAncestorsInferrer.infer(Term) shouldBe Nil
  }

  test("infer without name, when has enclosing template but it has no ancestors - should return empty") {
    val template = template"A"

    when(innermostEnclosingTemplateInferrer.infer(eqTree(Term), eqTo(None))).thenReturn(Some(template))
    when(templateAncestorsCollector.collect(any[Template])).thenReturn(Nil)

    innermostEnclosingTemplateAncestorsInferrer.infer(Term) shouldBe Nil
  }

  test("infer without name, when has enclosing template with ancestors - should return them") {
    val template = template"A with A1 with A2"

    when(innermostEnclosingTemplateInferrer.infer(eqTree(Term), eqTo(None))).thenReturn(Some(template))
    when(templateAncestorsCollector.collect(eqTree(template))).thenReturn(List(t"A1", t"A2"))

    innermostEnclosingTemplateAncestorsInferrer.infer(Term).structure shouldBe List(t"A1", t"A2").structure
  }

  test("infer with name, when has no enclosing templates - should return empty") {
    when(innermostEnclosingTemplateInferrer.infer(eqTree(Term), eqTo(Some(EnclosingMemberName)))).thenReturn(None)

    innermostEnclosingTemplateAncestorsInferrer.infer(Term, Some(EnclosingMemberName)) shouldBe Nil
  }

  test("infer with name, when has enclosing template but it has no ancestors - should return empty") {
    val template = template"A"

    when(innermostEnclosingTemplateInferrer.infer(eqTree(Term), eqTo(Some(EnclosingMemberName)))).thenReturn(Some(template))
    when(templateAncestorsCollector.collect(any[Template])).thenReturn(Nil)

    innermostEnclosingTemplateAncestorsInferrer.infer(Term, Some(EnclosingMemberName)) shouldBe Nil
  }

  test("infer with name, when has enclosing template with ancestors - should return them") {
    val template = template"A with A1 with A2"

    when(innermostEnclosingTemplateInferrer.infer(eqTree(Term), eqTo(Some(EnclosingMemberName)))).thenReturn(Some(template))
    when(templateAncestorsCollector.collect(eqTree(template))).thenReturn(List(t"A1", t"A2"))

    innermostEnclosingTemplateAncestorsInferrer.infer(Term, Some(EnclosingMemberName)).structure shouldBe List(t"A1", t"A2").structure
  }
}
