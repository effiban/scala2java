package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.qualifiers.TemplateParentsByContextQualifier.qualify
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{XtensionQuasiquoteTemplate, XtensionQuasiquoteType}

class TemplateParentsByContextQualifierTest extends UnitTestSuite {

  test("qualify with inits and self when all are qualified") {

    val initialTemplate =
      template"""
      A with B { c: C =>
      }
      """

    val expectedFinalTemplate =
      template"""
      qualA.A with qualB.B { c: qualC.C =>
      }
      """

    val context = QualificationContext(qualifiedTypeMap = Map(
      t"A" -> t"qualA.A",
      t"B" -> t"qualB.B",
      t"C" -> t"qualC.C"
    ))

    qualify(initialTemplate, context).structure shouldBe expectedFinalTemplate.structure
  }

  test("qualify with inits and self when only inits are qualified") {

    val initialTemplate =
      template"""
      A with B { c: C =>
      }
      """

    val expectedFinalTemplate =
      template"""
      qualA.A with qualB.B { c: C =>
      }
      """

    val context = QualificationContext(qualifiedTypeMap = Map(
      t"A" -> t"qualA.A",
      t"B" -> t"qualB.B"
    ))

    qualify(initialTemplate, context).structure shouldBe expectedFinalTemplate.structure
  }

  test("qualify with inits only when some are qualified") {

    val initialTemplate = template"A with B with C"

    val expectedFinalTemplate = template"qualA.A with qualB.B with C"

    val context = QualificationContext(qualifiedTypeMap = Map(
      t"A" -> t"qualA.A",
      t"B" -> t"qualB.B"
    ))

    qualify(initialTemplate, context).structure shouldBe expectedFinalTemplate.structure
  }

  test("qualify with inits only when none are qualified") {

    val template = template"A with B with C"

    val context = QualificationContext()

    qualify(template, context).structure shouldBe template.structure
  }
}
