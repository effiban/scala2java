package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher.eqQualificationContext
import io.github.effiban.scala2java.core.qualifiers.QualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Init, Self, Stat, Tree, XtensionQuasiquoteImporter, XtensionQuasiquoteInit, XtensionQuasiquoteSelf, XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm}

class TemplateUnqualifierImplTest extends UnitTestSuite {

  private val qualificationContext = QualificationContext(List(importer"dummy.dummy"))

  private val treeUnqualifier = mock[TreeUnqualifier]

  private val templateUnqualifier = new TemplateUnqualifierImpl(treeUnqualifier)

  test("unqualify") {
    val initialTemplate =
      template"""
        qualA.A with qualB.B { c: qualC.C =>
          val d = qualdd.dd
          val e = qualee.ee
        }
        """

    val expectedFinalTemplate =
      template"""
        A with B { c: C =>
          val d = dd
          val e = ee
        }
        """

    doAnswer((tree: Tree, _: QualificationContext) => tree match {
      case anInit: Init if anInit.structure == init"qualA.A".structure => init"A"
      case anInit: Init if anInit.structure == init"qualB.B".structure => init"B"
      case aSelf: Self if aSelf.structure == self"c: qualC.C".structure => self"c: C"
      case aStat: Stat if aStat.structure == q"val d = qualdd.dd".structure => q"val d = dd"
      case aStat: Stat if aStat.structure == q"val e = qualee.ee".structure => q"val e = ee"
      case aTree => aTree
    }).when(treeUnqualifier).unqualify(any[Tree], eqQualificationContext(qualificationContext))

    templateUnqualifier.unqualify(initialTemplate, qualificationContext).structure shouldBe expectedFinalTemplate.structure
  }
}
