package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.StatsByImportSplitter
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Importer, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class PkgUnqualifierImplTest extends UnitTestSuite {

  private val statsByImportSplitter = mock[StatsByImportSplitter]
  private val typeSelectUnqualifier = mock[TypeSelectUnqualifier]

  private val pkgUnqualifier = new PkgUnqualifierImpl(
    statsByImportSplitter,
    typeSelectUnqualifier
  )

  test("unqualify when has no nested trees that could be unqualified, should return unchanged") {
    val pkg =
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
        def doC(): C
        def doD(): D
      }
      """
    )

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(pkg.stats))

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
    doAnswer((typeSelect: Type.Select, _: List[Importer]) => typeSelect match {
      case aTypeSelect if aTypeSelect.structure == t"c.C".structure => t"C"
      case aTypeSelect if aTypeSelect.structure == t"d.D".structure => t"D"
      case aTypeSelect => aTypeSelect
    }).when(typeSelectUnqualifier).unqualify(any[Type.Select], eqTreeList(expectedImporters))

    pkgUnqualifier.unqualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }
}
