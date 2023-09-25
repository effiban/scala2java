package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.StatsByImportSplitter
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.any
import org.mockito.Mockito.verifyNoInteractions

import scala.meta.{Importer, Term, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class PkgUnqualifierImplTest extends UnitTestSuite {

  private val statsByImportSplitter = mock[StatsByImportSplitter]
  private val termApplyUnqualifier = mock[TermApplyUnqualifier]
  private val typeSelectUnqualifier = mock[TypeSelectUnqualifier]

  private val pkgUnqualifier = new PkgUnqualifierImpl(
    statsByImportSplitter,
    termApplyUnqualifier,
    typeSelectUnqualifier
  )

  test("unqualify when has no nested trees that could be unqualified, should return unchanged") {
    val pkg =
      q"""
      package a.b {
        import c.C
        import d.D
      }
      """

    val expectedImporters = List(importer"c.C", importer"d.D")

    doReturn((expectedImporters, Nil)).when(statsByImportSplitter).split(eqTreeList(pkg.stats))

    pkgUnqualifier.unqualify(pkg).structure shouldBe pkg.structure

    verifyNoInteractions(termApplyUnqualifier, typeSelectUnqualifier)
  }

  test("unqualify when has nested Term.Apply-s but TermApplyUnqualifier returns unchanged, should return unchanged") {
    val pkg =
      q"""
      package a.b {
        import c.C
        import d.D

        trait MyTrait {
          def foo(): Unit = {
            doSomething()
          }
        }
      }
      """

    val expectedImporters = List(importer"c.C", importer"d.D")
    val expectedNonImports = List(
      q"""
      trait MyTrait {
        def foo(): Unit = {
          doSomething()
        }
      }
      """
    )

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(pkg.stats))
    doAnswer((termApply: Term.Apply) => termApply).when(termApplyUnqualifier).unqualify(any[Term.Apply], eqTreeList(expectedImporters))

    pkgUnqualifier.unqualify(pkg).structure shouldBe pkg.structure
  }

  test("unqualify when has nested Type.Select-s but TypeSelectUnqualifier returns unchanged, should return unchanged") {
    val pkg =
      q"""
      package a.b {
        import c.C
        import d.D

        trait MyTrait1 {
          val t: T.t
        }
        trait MyTrait2 {
          val u: U.u
        }
      }
      """

    val expectedImporters = List(importer"c.C", importer"d.D")
    val expectedNonImports = List(
      q"""
      trait MyTrait1 {
        val t: T.t
      }
      """,
      q"""
      trait MyTrait2 {
        val u: U.u
      }
      """
    )
    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(pkg.stats))
    doAnswer((typeSelect: Type.Select) => typeSelect).when(typeSelectUnqualifier).unqualify(any[Type.Select], eqTreeList(expectedImporters))

    pkgUnqualifier.unqualify(pkg).structure shouldBe pkg.structure
  }

  test("unqualify when TermApplyUnqualifier unqualifies some of the Term.Apply-s, should return them unqualified") {
    val initialPkg =
      q"""
      package a.b {
        import c.c1
        import d.d1

        trait MyTrait {
          def foo(): Unit = {
            c.c1()
            d.d1()
          }
        }
      }
      """

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.c1
        import d.d1

        trait MyTrait {
          def foo(): Unit = {
            c1()
            d1()
          }
        }
      }
      """

    val expectedImporters = List(importer"c.c1", importer"d.d1")
    val expectedNonImports = List(
      q"""
      trait MyTrait {
        def foo(): Unit = {
          c.c1()
          d.d1()
        }
      }
      """
    )

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termApply: Term.Apply, _: List[Importer]) => termApply match {
      case aTermApply if aTermApply.structure == q"c.c1()".structure => q"c1()"
      case aTermApply if aTermApply.structure == q"d.d1()".structure => q"d1()"
      case aTermApply => aTermApply
    }).when(termApplyUnqualifier).unqualify(any[Term.Apply], eqTreeList(expectedImporters))

    pkgUnqualifier.unqualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("unqualify when TypeSelectUnqualifier unqualifies some of the Type.Selects, should return them unqualified") {
    val initialPkg =
      q"""
      package a.b {
        import c.C
        import d.D

        trait MyTrait {
          def doC(): c.C
          def doD(): d.D
        }
      }
      """

    val expectedFinalPkg =
      q"""
      package a.b {
        import c.C
        import d.D

        trait MyTrait {
          def doC(): C
          def doD(): D
        }
      }
      """

    val expectedImporters = List(importer"c.C", importer"d.D")
    val expectedNonImports = List(
      q"""
      trait MyTrait {
        def doC(): c.C
        def doD(): d.D
      }
      """
    )

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termApply: Term.Apply) => termApply).when(termApplyUnqualifier).unqualify(any[Term.Apply], eqTreeList(expectedImporters))
    doAnswer((typeSelect: Type.Select, _: List[Importer]) => typeSelect match {
      case aTypeSelect if aTypeSelect.structure == t"c.C".structure => t"C"
      case aTypeSelect if aTypeSelect.structure == t"d.D".structure => t"D"
      case aTypeSelect => aTypeSelect
    }).when(typeSelectUnqualifier).unqualify(any[Type.Select], eqTreeList(expectedImporters))

    pkgUnqualifier.unqualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }
}
