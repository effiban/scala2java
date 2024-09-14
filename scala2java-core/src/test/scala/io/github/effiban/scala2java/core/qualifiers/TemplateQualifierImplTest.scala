package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Init, Self, Stat, Tree, XtensionQuasiquoteImporter, XtensionQuasiquoteInit, XtensionQuasiquoteSelf, XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm}

class TemplateQualifierImplTest extends UnitTestSuite {

  private val treeQualifier = mock[TreeQualifier]

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

    val importers = List(importer"dummy1.dummy1", importer"dummy2.dummy2")
    val parentContext = QualificationContext(importers = importers)

    val expectedChildContext = QualificationContext(importers = importers)

    doAnswer((tree: Tree, context: QualificationContext) => {
      val parentContextMatches = new QualificationContextMockitoMatcher(parentContext).matches(context)
      val childContextMatches = new QualificationContextMockitoMatcher(expectedChildContext).matches(context)

      tree match {
        case init: Init if init.structure == init"A".structure && parentContextMatches => init"qualA.A"
        case init: Init if init.structure == init"B".structure && parentContextMatches => init"qualB.B"
        case self: Self if self.structure == self"c: C".structure && parentContextMatches => self"c: qualC.C"
        case stat: Stat if stat.structure == q"val d = dd".structure && childContextMatches => q"val d = qualdd.dd"
        case stat: Stat if stat.structure == q"val e = ee".structure && childContextMatches => q"val e = qualee.ee"
        case aTree => aTree
      }
    }).when(treeQualifier).qualify(any[Tree], any[QualificationContext])

    templateQualifier.qualify(initialTemplate, parentContext).structure shouldBe expectedFinalTemplate.structure
  }
}
