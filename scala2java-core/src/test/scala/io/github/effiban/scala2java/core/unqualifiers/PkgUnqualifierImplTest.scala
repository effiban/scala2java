package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.ImporterCollector
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Importer, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class PkgUnqualifierImplTest extends UnitTestSuite {

  private val importerCollector = mock[ImporterCollector]
  private val typeSelectUnqualifier = mock[TypeSelectUnqualifier]

  private val pkgUnqualifier = new PkgUnqualifierImpl(importerCollector, typeSelectUnqualifier)

  test("unqualify when has no nested Type.Selects should return unchanged") {
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

    doReturn(expectedImporters).when(importerCollector).collectFlat(eqTreeList(pkg.stats))

    pkgUnqualifier.unqualify(pkg).structure shouldBe pkg.structure
  }

  test("unqualify when has nested Type.Selects should return them unqualified") {
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

    doReturn(expectedImporters).when(importerCollector).collectFlat(eqTreeList(initialPkg.stats))
    doAnswer((typeSelect: Type.Select, _: List[Importer]) => typeSelect match {
      case aTypeSelect if aTypeSelect.structure == t"c.C".structure => t"C"
      case aTypeSelect if aTypeSelect.structure == t"d.D".structure => t"D"
    }).when(typeSelectUnqualifier).unqualify(any[Type.Select], eqTreeList(expectedImporters))

    pkgUnqualifier.unqualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }
}
