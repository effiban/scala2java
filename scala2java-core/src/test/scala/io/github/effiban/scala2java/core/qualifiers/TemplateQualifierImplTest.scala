package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Init, Self, Stat, Tree, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteInit, XtensionQuasiquoteSelf, XtensionQuasiquoteTemplate, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TemplateQualifierImplTest extends UnitTestSuite {

  private val treeQualifier = mock[TreeQualifier]

  private val templateQualifier = new TemplateQualifierImpl(treeQualifier)

  test("qualify when template has all children types and all are qualified") {

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

    val parentQualifiedTypeMap = Map[Type, Type](t"X" -> t"qualX.X")

    val childQualifiedTypeMap = Map[Type, Type](
      t"A" -> t"qualA.A",
      t"B" -> t"qualB.B",
      t"C" -> t"qualC.C",
      t"X" -> t"qualX.X",
    )

    val parentContext = QualificationContext(importers = importers, qualifiedTypeMap = parentQualifiedTypeMap)

    val expectedChildContext = QualificationContext(importers = importers, qualifiedTypeMap = childQualifiedTypeMap)

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

    val actualFinalTemplate = templateQualifier.qualify(initialTemplate, parentContext)
    actualFinalTemplate.structure shouldBe expectedFinalTemplate.structure
  }

  test("qualify when template has all children types and only inits and self are qualified") {

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
        val d = dd
        val e = ee
      }
      """

    val importers = List(importer"dummy1.dummy1", importer"dummy2.dummy2")

    val parentQualifiedTypeMap = Map[Type, Type](t"X" -> t"qualX.X")

    val parentContext = QualificationContext(importers = importers, qualifiedTypeMap = parentQualifiedTypeMap)

    doAnswer((tree: Tree, context: QualificationContext) => {
      val parentContextMatches = new QualificationContextMockitoMatcher(parentContext).matches(context)

      tree match {
        case init: Init if init.structure == init"A".structure && parentContextMatches => init"qualA.A"
        case init: Init if init.structure == init"B".structure && parentContextMatches => init"qualB.B"
        case self: Self if self.structure == self"c: C".structure && parentContextMatches => self"c: qualC.C"
        case aTree => aTree
      }
    }).when(treeQualifier).qualify(any[Tree], any[QualificationContext])

    val actualFinalTemplate = templateQualifier.qualify(initialTemplate, parentContext)
    actualFinalTemplate.structure shouldBe expectedFinalTemplate.structure
  }

  test("qualify when template has inits and self only, and only inits are qualified") {

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

    val importers = List(importer"dummy1.dummy1", importer"dummy2.dummy2")

    val parentQualifiedTypeMap = Map[Type, Type](t"X" -> t"qualX.X")

    val parentContext = QualificationContext(importers = importers, qualifiedTypeMap = parentQualifiedTypeMap)

    doAnswer((tree: Tree, context: QualificationContext) => {
      val parentContextMatches = new QualificationContextMockitoMatcher(parentContext).matches(context)

      tree match {
        case init: Init if init.structure == init"A".structure && parentContextMatches => init"qualA.A"
        case init: Init if init.structure == init"B".structure && parentContextMatches => init"qualB.B"
        case aTree => aTree
      }
    }).when(treeQualifier).qualify(any[Tree], any[QualificationContext])

    val actualFinalTemplate = templateQualifier.qualify(initialTemplate, parentContext)
    actualFinalTemplate.structure shouldBe expectedFinalTemplate.structure
  }

  test("qualify when template has inits only, and only some are qualified") {

    val initialTemplate = template"A with B with C"

    val expectedFinalTemplate = template"qualA.A with qualB.B with C"

    val importers = List(importer"dummy1.dummy1", importer"dummy2.dummy2")

    val parentQualifiedTypeMap = Map[Type, Type](t"X" -> t"qualX.X")

    val parentContext = QualificationContext(importers = importers, qualifiedTypeMap = parentQualifiedTypeMap)

    doAnswer((tree: Tree, context: QualificationContext) => {
      val parentContextMatches = new QualificationContextMockitoMatcher(parentContext).matches(context)

      tree match {
        case init: Init if init.structure == init"A".structure && parentContextMatches => init"qualA.A"
        case init: Init if init.structure == init"B".structure && parentContextMatches => init"qualB.B"
        case aTree => aTree
      }
    }).when(treeQualifier).qualify(any[Tree], any[QualificationContext])

    val actualFinalTemplate = templateQualifier.qualify(initialTemplate, parentContext)
    actualFinalTemplate.structure shouldBe expectedFinalTemplate.structure
  }
}
