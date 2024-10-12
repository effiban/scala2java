package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.matchers.QualificationContextMockitoMatcher
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Init, Self, Stat, Tree, XtensionQuasiquoteImporter, XtensionQuasiquoteInit, XtensionQuasiquoteSelf, XtensionQuasiquoteTerm}

class TemplateQualifierImplTest extends UnitTestSuite {

  private val treeQualifier = mock[TreeQualifier]

  private val templateQualifier = new TemplateQualifierImpl(treeQualifier)

  test("qualify for a template of a class") {

    val initialClass =
      q"""
      class MyClass extends A with B { c: C =>
        val d = dd
        val e = ee
      }
      """
    val initialTemplate = initialClass.templ

    val expectedFinalClass =
      q"""
      class MyClass extends qualA.A with qualB.B { c: qualC.C =>
        val d = qualdd.dd
        val e = qualee.ee
      }
      """

    val expectedFinalTemplate = expectedFinalClass.templ

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

    val actualFinalTemplate = templateQualifier.qualify(initialTemplate, parentContext)
    actualFinalTemplate.structure shouldBe expectedFinalTemplate.structure
    actualFinalTemplate.parent.value.structure shouldBe expectedFinalClass.structure
  }

  test("qualify for a template of a trait") {

    val initialTrait =
      q"""
      trait MyTrait extends A { c: C =>
        val d = dd
      }
      """
    val initialTemplate = initialTrait.templ

    val expectedFinalTrait =
      q"""
      trait MyTrait extends qualA.A { c: qualC.C =>
        val d = qualdd.dd
      }
      """

    val expectedFinalTemplate = expectedFinalTrait.templ

    val importers = List(importer"dummy1.dummy1", importer"dummy2.dummy2")
    val parentContext = QualificationContext(importers = importers)

    val expectedChildContext = QualificationContext(importers = importers)

    doAnswer((tree: Tree, context: QualificationContext) => {
      val parentContextMatches = new QualificationContextMockitoMatcher(parentContext).matches(context)
      val childContextMatches = new QualificationContextMockitoMatcher(expectedChildContext).matches(context)

      tree match {
        case init: Init if init.structure == init"A".structure && parentContextMatches => init"qualA.A"
        case self: Self if self.structure == self"c: C".structure && parentContextMatches => self"c: qualC.C"
        case stat: Stat if stat.structure == q"val d = dd".structure && childContextMatches => q"val d = qualdd.dd"
        case aTree => aTree
      }
    }).when(treeQualifier).qualify(any[Tree], any[QualificationContext])

    val actualFinalTemplate = templateQualifier.qualify(initialTemplate, parentContext)
    actualFinalTemplate.structure shouldBe expectedFinalTemplate.structure
    actualFinalTemplate.parent.value.structure shouldBe expectedFinalTrait.structure
  }

  test("qualify for a template of an object") {

    val initialObject =
      q"""
      object MyObject extends A { c: C =>
        val d = dd
      }
      """
    val initialTemplate = initialObject.templ

    val expectedFinalObject =
      q"""
      object MyObject extends qualA.A { c: qualC.C =>
        val d = qualdd.dd
      }
      """

    val expectedFinalTemplate = expectedFinalObject.templ

    val importers = List(importer"dummy1.dummy1", importer"dummy2.dummy2")
    val parentContext = QualificationContext(importers = importers)

    val expectedChildContext = QualificationContext(importers = importers)

    doAnswer((tree: Tree, context: QualificationContext) => {
      val parentContextMatches = new QualificationContextMockitoMatcher(parentContext).matches(context)
      val childContextMatches = new QualificationContextMockitoMatcher(expectedChildContext).matches(context)

      tree match {
        case init: Init if init.structure == init"A".structure && parentContextMatches => init"qualA.A"
        case self: Self if self.structure == self"c: C".structure && parentContextMatches => self"c: qualC.C"
        case stat: Stat if stat.structure == q"val d = dd".structure && childContextMatches => q"val d = qualdd.dd"
        case aTree => aTree
      }
    }).when(treeQualifier).qualify(any[Tree], any[QualificationContext])

    val actualFinalTemplate = templateQualifier.qualify(initialTemplate, parentContext)
    actualFinalTemplate.structure shouldBe expectedFinalTemplate.structure
    actualFinalTemplate.parent.value.structure shouldBe expectedFinalObject.structure
  }

  test("qualify for a template of a NewAnonymous") {

    val initialNewAnonymous =
      q"""
      new A {
        val d = dd
      }
      """
    val initialTemplate = initialNewAnonymous.templ

    val expectedFinalNewAnonymous =
      q"""
      new qualA.A {
        val d = qualdd.dd
      }
      """

    val expectedFinalTemplate = expectedFinalNewAnonymous.templ

    val importers = List(importer"dummy1.dummy1", importer"dummy2.dummy2")
    val parentContext = QualificationContext(importers = importers)

    val expectedChildContext = QualificationContext(importers = importers)

    doAnswer((tree: Tree, context: QualificationContext) => {
      val parentContextMatches = new QualificationContextMockitoMatcher(parentContext).matches(context)
      val childContextMatches = new QualificationContextMockitoMatcher(expectedChildContext).matches(context)

      tree match {
        case init: Init if init.structure == init"A".structure && parentContextMatches => init"qualA.A"
        case stat: Stat if stat.structure == q"val d = dd".structure && childContextMatches => q"val d = qualdd.dd"
        case aTree => aTree
      }
    }).when(treeQualifier).qualify(any[Tree], any[QualificationContext])

    val actualFinalTemplate = templateQualifier.qualify(initialTemplate, parentContext)
    actualFinalTemplate.structure shouldBe expectedFinalTemplate.structure
    actualFinalTemplate.parent.value.structure shouldBe expectedFinalNewAnonymous.structure
  }
}
