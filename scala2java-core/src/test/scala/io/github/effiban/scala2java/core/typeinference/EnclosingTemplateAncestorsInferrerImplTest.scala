package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.collectors.TemplateAncestorsCollector
import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Template, XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class EnclosingTemplateAncestorsInferrerImplTest extends UnitTestSuite {

  private val enclosingTemplatesInferrer = mock[EnclosingTemplatesInferrer]
  private val templateAncestorsCollector = mock[TemplateAncestorsCollector]
  
  private val enclosingTemplateAncestorsInferrer = new EnclosingTemplateAncestorsInferrerImpl(
    enclosingTemplatesInferrer,
    templateAncestorsCollector
  )

  private val term = q"x"
  
  test("infer when has no enclosing templates should return empty") {
    when(enclosingTemplatesInferrer.infer(eqTree(term))).thenReturn(Nil)

    enclosingTemplateAncestorsInferrer.infer(term) shouldBe Map.empty
  }

  test("infer when has enclosing templates but none have ancestors should return empty") {
    val templA = template"A"
    val templB = template"B"

    when(enclosingTemplatesInferrer.infer(eqTree(term))).thenReturn(List(templA, templB))
    when(templateAncestorsCollector.collect(any[Template])).thenReturn(Nil)

    enclosingTemplateAncestorsInferrer.infer(term) shouldBe Map.empty
  }

  test("infer when has enclosing templates and some have ancestors should return them") {
    val templA = template"A with A1 with A2"
    val templB = template"B"

    when(enclosingTemplatesInferrer.infer(eqTree(term))).thenReturn(List(templA, templB))
    doAnswer((template: Template) => template match {
      case templ: Template if templ.structure == templA.structure => List(t"A1", t"A2")
      case _ => Nil
    }).when(templateAncestorsCollector).collect(any[Template])

    val enclosingTemplateAncestors = enclosingTemplateAncestorsInferrer.infer(term)
    enclosingTemplateAncestors.size shouldBe 1
    val (enclosingTemplate, enclosingAncestors) = enclosingTemplateAncestors.head
    enclosingTemplate.structure shouldBe templA.structure
    enclosingAncestors.structure shouldBe List(t"A1", t"A2").structure
  }

  test("infer when has enclosing templates and all have ancestors should return them") {
    val templA = template"A with A1 with A2"
    val templB = template"B with B1 with B2"

    when(enclosingTemplatesInferrer.infer(eqTree(term))).thenReturn(List(templA, templB))
    doAnswer((template: Template) => template match {
      case templ: Template if templ.structure == templA.structure => List(t"A1", t"A2")
      case templ: Template if templ.structure == templB.structure => List(t"B1", t"B2")
      case _ => Nil
    }).when(templateAncestorsCollector).collect(any[Template])

    val enclosingTemplateAncestors = enclosingTemplateAncestorsInferrer.infer(term)
    enclosingTemplateAncestors.size shouldBe 2

    val enclosingAncestorsA = TreeKeyedMap(enclosingTemplateAncestors, templA)
    enclosingAncestorsA.structure shouldBe List(t"A1", t"A2").structure

    val enclosingAncestorsB = TreeKeyedMap(enclosingTemplateAncestors, templB)
    enclosingAncestorsB.structure shouldBe List(t"B1", t"B2").structure
  }
}
