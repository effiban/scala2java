package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.collectors.TemplateAncestorsCollector
import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher.eqQualificationContext
import io.github.effiban.scala2java.core.qualifiers.{QualificationContext, TemplateParentsByContextQualifier}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTemplate, XtensionQuasiquoteType}

class TemplateAncestorsInferrerImplTest extends UnitTestSuite {

  private val templateAncestorsCollector = mock[TemplateAncestorsCollector]
  private val templateParentsByContextQualifier = mock[TemplateParentsByContextQualifier]

  private val templateAncestorsInferrer = new TemplateAncestorsInferrerImpl(
    templateAncestorsCollector,
    templateParentsByContextQualifier
  )

  test("infer when the template has only parents (direct ancestors) should return them") {
    val templ = template"A with B"
    val qualifiedTempl = template"qualA.A with qualB.B"
    val ancestors = List(t"qualA.A", t"qualB.B")

    val context = QualificationContext(qualifiedTypeMap = Map(
      t"A" -> t"qualA.A",
      t"B" -> t"qualB.B",
    ))

    when(templateParentsByContextQualifier.qualify(eqTree(templ), eqQualificationContext(context))).thenReturn(qualifiedTempl)
    when(templateAncestorsCollector.collect(qualifiedTempl)).thenReturn(ancestors)

    templateAncestorsInferrer.infer(templ, context).structure shouldBe ancestors.structure
  }

  test("infer when the template has indirect ancestors - should return all the ancestors") {
    val templ = template"A with B"
    val qualifiedTempl = template"qualA.A with qualB.B"
    val ancestors = List(
      t"qualA.A",
      t"qualB.B",
      t"qualC.C",
      t"qualD.D",
    )

    val context = QualificationContext(qualifiedTypeMap = Map(
      t"A" -> t"qualA.A",
      t"B" -> t"qualB.B",
    ))

    when(templateParentsByContextQualifier.qualify(eqTree(templ), eqQualificationContext(context))).thenReturn(qualifiedTempl)
    when(templateAncestorsCollector.collect(qualifiedTempl)).thenReturn(ancestors)

    templateAncestorsInferrer.infer(templ, context).structure shouldBe ancestors.structure
  }
}
