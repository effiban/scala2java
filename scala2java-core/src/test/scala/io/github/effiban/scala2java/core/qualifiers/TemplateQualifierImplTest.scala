package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher.eqQualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Init, Self, Stat, Tree, XtensionQuasiquoteImporter, XtensionQuasiquoteInit, XtensionQuasiquoteSelf, XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm}

class TemplateQualifierImplTest extends UnitTestSuite {

  private val treeQualifier = mock[TreeQualifier]

  private val qualificationContext = QualificationContext(List(importer"dummy.dummy"))

  private val templateQualifier = new TemplateQualifierImpl(treeQualifier)

  test("qualify") {
    val initialTemplate =
      template"""
      A with B { c: C =>
        val d = dd
        val e = ee
      }
      """

    val expectedFinalTemplate =
      template"""
      qualA.A with qualB.B { c: qualC.C =>
        val d = qualdd.dd
        val e = qualee.ee
      }
      """

    doAnswer((tree: Tree, _: QualificationContext) => tree match {
      case anInit: Init if anInit.structure == init"A".structure => init"qualA.A"
      case anInit: Init if anInit.structure == init"B".structure => init"qualB.B"
      case aSelf: Self if aSelf.structure == self"c: C".structure => self"c: qualC.C"
      case aStat: Stat if aStat.structure == q"val d = dd".structure => q"val d = qualdd.dd"
      case aStat: Stat if aStat.structure == q"val e = ee".structure => q"val e = qualee.ee"
      case aTree => aTree
    }).when(treeQualifier).qualify(any[Tree], eqQualificationContext(qualificationContext))

    templateQualifier.qualify(initialTemplate, qualificationContext).structure shouldBe expectedFinalTemplate.structure
  }
}
