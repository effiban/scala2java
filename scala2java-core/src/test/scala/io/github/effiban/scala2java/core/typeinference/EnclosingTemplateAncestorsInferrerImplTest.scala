package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.collectors.TemplateAncestorsCollector
import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher.eqQualificationContext
import io.github.effiban.scala2java.core.qualifiers.{QualificationContext, TemplateByContextQualifier}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Template, XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class EnclosingTemplateAncestorsInferrerImplTest extends UnitTestSuite {

  private val enclosingTemplatesInferrer = mock[EnclosingTemplatesInferrer]
  private val templateAncestorsCollector = mock[TemplateAncestorsCollector]
  private val templateByContextQualifier = mock[TemplateByContextQualifier]
  
  private val enclosingTemplateAncestorsInferrer = new EnclosingTemplateAncestorsInferrerImpl(
    enclosingTemplatesInferrer,
    templateAncestorsCollector,
    templateByContextQualifier
  )

  private val term = q"x"
  
  test("infer when has no enclosing templates should return empty") {
    when(enclosingTemplatesInferrer.infer(eqTree(term))).thenReturn(Nil)

    enclosingTemplateAncestorsInferrer.infer(term, QualificationContext()) shouldBe Map.empty
  }

  test("infer when has enclosing templates, unqualified, but none have ancestors should return empty") {
    val templA = template"A"
    val templB = template"B"

    val context = QualificationContext()

    when(enclosingTemplatesInferrer.infer(eqTree(term))).thenReturn(List(templA, templB))
    doAnswer((templ: Template) => templ).when(templateByContextQualifier).qualify(any[Template], eqQualificationContext(context))

    when(templateAncestorsCollector.collect(any[Template])).thenReturn(Nil)

    enclosingTemplateAncestorsInferrer.infer(term, QualificationContext()) shouldBe Map.empty
  }

  test("infer when has enclosing templates, qualified, but none have ancestors should return empty") {
    val templA = template"A with A1"
    val templB = template"B with B1"

    val qualifiedTemplA = template"A with qualA.A1"
    val qualifiedTemplB = template"B with qualB.B1"

    val context = QualificationContext(qualifiedTypeMap = Map(
      t"A1" -> t"qualA.A1",
      t"B1" -> t"qualB.B1",
    ))

    when(enclosingTemplatesInferrer.infer(eqTree(term))).thenReturn(List(templA, templB))
    doAnswer((templ: Template, _: QualificationContext) => templ match {
      case aTempl if aTempl.structure == templA.structure => qualifiedTemplA
      case aTempl if aTempl.structure == templB.structure => qualifiedTemplB
      case _ => templ
    }).when(templateByContextQualifier).qualify(any[Template], eqQualificationContext(context))
    when(templateAncestorsCollector.collect(any[Template])).thenReturn(Nil)

    enclosingTemplateAncestorsInferrer.infer(term, context) shouldBe Map.empty

    verify(templateAncestorsCollector).collect(eqTree(qualifiedTemplA))
    verify(templateAncestorsCollector).collect(eqTree(qualifiedTemplB))
  }

  test("infer when has enclosing templates, unqualified, and some have ancestors - should return them") {
    val templA = template"A with A1 with A2"
    val templB = template"B"

    val context = QualificationContext()

    when(enclosingTemplatesInferrer.infer(eqTree(term))).thenReturn(List(templA, templB))
    doAnswer((templ: Template) => templ).when(templateByContextQualifier).qualify(any[Template], eqQualificationContext(context))
    doAnswer((template: Template) => template match {
      case templ: Template if templ.structure == templA.structure => List(t"A1", t"A2")
      case _ => Nil
    }).when(templateAncestorsCollector).collect(any[Template])

    val enclosingTemplateAncestors = enclosingTemplateAncestorsInferrer.infer(term, context)
    enclosingTemplateAncestors.size shouldBe 1
    val (enclosingTemplate, enclosingAncestors) = enclosingTemplateAncestors.head
    enclosingTemplate.structure shouldBe templA.structure
    enclosingAncestors.structure shouldBe List(t"A1", t"A2").structure
  }

  test("infer when has enclosing templates, qualified, and some have ancestors - should return them") {
    val templA = template"A with A1 with A2"
    val templB = template"B"


    val qualifiedTemplA = template"A with qualA.A1 with qualA.A2"
    val qualifiedTemplB = template"B with qualB.B1"

    val context = QualificationContext(qualifiedTypeMap = Map(
      t"A1" -> t"qualA.A1",
      t"A2" -> t"qualA.A2",
      t"B1" -> t"qualB.B1",
    ))

    when(enclosingTemplatesInferrer.infer(eqTree(term))).thenReturn(List(templA, templB))
    doAnswer((templ: Template, _: QualificationContext) => templ match {
      case aTempl if aTempl.structure == templA.structure => qualifiedTemplA
      case aTempl if aTempl.structure == templB.structure => qualifiedTemplB
      case _ => templ
    }).when(templateByContextQualifier).qualify(any[Template], eqQualificationContext(context))
    doAnswer((template: Template) => template match {
      case templ: Template if templ.structure == qualifiedTemplA.structure => List(t"qualA.A1", t"qualA.A2")
      case _ => Nil
    }).when(templateAncestorsCollector).collect(any[Template])

    val enclosingTemplateAncestors = enclosingTemplateAncestorsInferrer.infer(term, context)
    enclosingTemplateAncestors.size shouldBe 1
    val (enclosingTemplate, enclosingAncestors) = enclosingTemplateAncestors.head
    enclosingTemplate.structure shouldBe templA.structure
    enclosingAncestors.structure shouldBe List(t"qualA.A1", t"qualA.A2").structure
  }

  test("infer when has enclosing templates and all have ancestors should return them") {
    val templA = template"A with A1 with A2"
    val templB = template"B with B1 with B2"

    val context = QualificationContext()

    when(enclosingTemplatesInferrer.infer(eqTree(term))).thenReturn(List(templA, templB))
    doAnswer((templ: Template) => templ).when(templateByContextQualifier).qualify(any[Template], eqQualificationContext(context))
    doAnswer((template: Template) => template match {
      case templ: Template if templ.structure == templA.structure => List(t"A1", t"A2")
      case templ: Template if templ.structure == templB.structure => List(t"B1", t"B2")
      case _ => Nil
    }).when(templateAncestorsCollector).collect(any[Template])

    val enclosingTemplateAncestors = enclosingTemplateAncestorsInferrer.infer(term, context)
    enclosingTemplateAncestors.size shouldBe 2

    val enclosingAncestorsA = TreeKeyedMap(enclosingTemplateAncestors, templA)
    enclosingAncestorsA.structure shouldBe List(t"A1", t"A2").structure

    val enclosingAncestorsB = TreeKeyedMap(enclosingTemplateAncestors, templB)
    enclosingAncestorsB.structure shouldBe List(t"B1", t"B2").structure
  }
}
