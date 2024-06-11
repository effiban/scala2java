package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.qualifiers.QualificationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.any
import org.mockito.Mockito.verifyNoInteractions

import scala.meta.{Importer, Term, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TreeUnqualifierImplTest extends UnitTestSuite {

  private val termSelectUnqualifier = mock[TermSelectUnqualifier]
  private val typeSelectUnqualifier = mock[TypeSelectUnqualifier]
  private val typeProjectUnqualifier = mock[TypeProjectUnqualifier]

  private val importers = List(importer"dummy.dummy", importer"dummy.dummy2")

  private val treeUnqualifier = new TreeUnqualifierImpl(
    termSelectUnqualifier,
    typeSelectUnqualifier,
    typeProjectUnqualifier
  )

  test("unqualify when has no nested trees that could be unqualified, should return unchanged") {
    val tree =
      q"""
      def foo: Unit = {
        val x = 3
      }
      """

    treeUnqualifier.unqualify(tree, importers).structure shouldBe tree.structure

    verifyNoInteractions(termSelectUnqualifier, typeSelectUnqualifier, typeProjectUnqualifier)
  }

  test("unqualify when has nested Term.Select-s but TermSelectUnqualifier returns unchanged, should return unchanged") {
    val tree =
      q"""
      def foo(): Unit = {
        a.doSomething()
      }
      """

    doAnswer((termSelect: Term.Select) => termSelect)
      .when(termSelectUnqualifier).unqualify(any[Term.Select], eqTreeList(importers))

    treeUnqualifier.unqualify(tree, importers).structure shouldBe tree.structure
  }

  test("unqualify when has nested Type.Select-s but TypeSelectUnqualifier returns unchanged, should return unchanged") {
    val tree =
      q"""
      def foo(): Unit = {
        var t: T.TT
        var u: U.UU
      }
      """

    doAnswer((typeSelect: Type.Select) => typeSelect)
      .when(typeSelectUnqualifier).unqualify(any[Type.Select], eqTreeList(importers))

    treeUnqualifier.unqualify(tree, importers).structure shouldBe tree.structure
  }

  test("unqualify when has nested Type.Project-s but TypeProjectUnqualifier returns unchanged, should return unchanged") {
    val tree =
      q"""
      def foo(): Unit = {
        var e: E#F
        var g: G#H
      }
      """

    doAnswer((typeProject: Type.Project) => typeProject)
      .when(typeProjectUnqualifier).unqualify(any[Type.Project], eqTreeList(importers))

    treeUnqualifier.unqualify(tree, importers).structure shouldBe tree.structure
  }

  test("unqualify when TermSelectUnqualifier unqualifies some of the Term.Select-s by full match, should return them unqualified") {
    val initialTree =
      q"""
      def foo(): Unit = {
        c.c1()
        d.d1()
      }
      """

    val expectedFinalTree =
      q"""
      def foo(): Unit = {
        c1()
        d1()
      }
      """

    doAnswer((termSelect: Term.Select, _: List[Importer]) => termSelect match {
      case aTermSelect if aTermSelect.structure == q"c.c1".structure => q"c1"
      case aTermSelect if aTermSelect.structure == q"d.d1".structure => q"d1"
      case aTermSelect => aTermSelect
    }).when(termSelectUnqualifier).unqualify(any[Term.Select], eqTreeList(importers))

    treeUnqualifier.unqualify(initialTree, importers).structure shouldBe expectedFinalTree.structure
  }

  test("unqualify when TermSelectUnqualifier unqualifies some Term.Select-s by prefix match, should return them unqualified") {
    val initialTree =
      q"""
      def foo(): Unit = {
        c1.c2.c3()
        d1.d2.d3()
      }
      """

    val expectedFinalTree =
      q"""
      def foo(): Unit = {
        c2.c3()
        d2.d3()
      }
      """

    doAnswer((termSelect: Term.Select, _: List[Importer]) => termSelect match {
      case aTermSelect if aTermSelect.structure == q"c1.c2".structure => q"c2"
      case aTermSelect if aTermSelect.structure == q"d1.d2".structure => q"d2"
      case aTermSelect => aTermSelect
    }).when(termSelectUnqualifier).unqualify(any[Term.Select], eqTreeList(importers))

    treeUnqualifier.unqualify(initialTree, importers).structure shouldBe expectedFinalTree.structure
  }

  test("unqualify when TypeSelectUnqualifier unqualifies some of the Type.Selects, should return them unqualified") {
    val initialTree =
      q"""
      def foo(): Unit = {
        var a: c.C
        var b: d.D
      }
      """

    val expectedFinalTree =
      q"""
      def foo(): Unit = {
        var a: C
        var b: D
      }
      """

    doAnswer((termSelect: Term.Select) => termSelect)
      .when(termSelectUnqualifier).unqualify(any[Term.Select], eqTreeList(importers))
    doAnswer((typeSelect: Type.Select, _: List[Importer]) => typeSelect match {
      case aTypeSelect if aTypeSelect.structure == t"c.C".structure => t"C"
      case aTypeSelect if aTypeSelect.structure == t"d.D".structure => t"D"
      case aTypeSelect => aTypeSelect
    }).when(typeSelectUnqualifier).unqualify(any[Type.Select], eqTreeList(importers))

    treeUnqualifier.unqualify(initialTree, importers).structure shouldBe expectedFinalTree.structure
  }

  test("unqualify when TypeProjectUnqualifier unqualifies some of the Type.Projects by full match, should return them unqualified") {
    val initialTree =
      q"""
      def foo(): Unit = {
        var x1: C#D
        var x2: E#F
      }
      """

    val expectedFinalTree =
      q"""
      def foo(): Unit = {
        var x1: D
        var x2: F
      }
      """

    doAnswer((termSelect: Term.Select) => termSelect)
      .when(termSelectUnqualifier).unqualify(any[Term.Select], eqTreeList(importers))
    doAnswer((typeProject: Type.Project, _: List[Importer]) => typeProject match {
      case aTypeProject if aTypeProject.structure == t"C#D".structure => t"D"
      case aTypeProject if aTypeProject.structure == t"E#F".structure => t"F"
      case aTypeProject => aTypeProject
    }).when(typeProjectUnqualifier).unqualify(any[Type.Project], eqTreeList(importers))

    treeUnqualifier.unqualify(initialTree, importers).structure shouldBe expectedFinalTree.structure
  }

  test("unqualify when TypeProjectUnqualifier unqualifies some of the Type.Projects by partial match, should return them unqualified") {
    val initialTree =
      q"""
      def foo(): Unit = {
        var x1: C1#C2#C3
        var x2: D1#D2#D3
      }
      """

    val expectedFinalTree =
      q"""
      def foo(): Unit = {
        var x1: C2#C3
        var x2: D2#D3
      }
      """

    doAnswer((termSelect: Term.Select) => termSelect)
      .when(termSelectUnqualifier).unqualify(any[Term.Select], eqTreeList(importers))
    doAnswer((typeProject: Type.Project, _: List[Importer]) => typeProject match {
      case aTypeProject if aTypeProject.structure == t"C1#C2".structure => t"C2"
      case aTypeProject if aTypeProject.structure == t"D1#D2".structure => t"D2"
      case aTypeProject => aTypeProject
    }).when(typeProjectUnqualifier).unqualify(any[Type.Project], eqTreeList(importers))

    treeUnqualifier.unqualify(initialTree, importers).structure shouldBe expectedFinalTree.structure
  }
}
