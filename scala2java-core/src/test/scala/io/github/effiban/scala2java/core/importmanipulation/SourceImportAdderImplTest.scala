package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Pkg, Source, XtensionQuasiquoteSource, XtensionQuasiquoteTerm}

class SourceImportAdderImplTest extends UnitTestSuite {

  private val pkgImportAdder = mock[PkgImportAdder]

  private val sourceImportAdder = new SourceImportAdderImpl(pkgImportAdder)

  test("addTo() when has no packages should return unchanged") {
    val source = Source(List(q"val x: Int = 3"))

    sourceImportAdder.addTo(source).structure shouldBe source.structure
  }

  test("addTo() when has packages should return a Source containing the packages with added imports") {
    val initialPkg1 =
      q"""
      package pkg1 {
        import a.B

        trait Trait1 { val x: c.D }
      }
      """

    val initialPkg2 =
      q"""
      package pkg2 {
        import e.F

        trait Trait2 { val y: g.H }
      }
      """

    val initialSource =
      source"""
      package pkg1 {
        import a.B

        trait Trait1 { val x: c.D }
      }
      package pkg2 {
        import e.F

        trait Trait2 { val y: g.H }
      }
      """

    val expectedFinalPkg1 =
      q"""
      package pkg1 {
        import a.B
        import c.D

        trait Trait1 { val x: c.D }
      }
      """

    val expectedFinalPkg2 =
      q"""
      package pkg2 {
        import e.F
        import g.H

        trait Trait2 { val y: g.H }
      }
      """

    val expectedFinalSource =
      source"""
      package pkg1 {
        import a.B
        import c.D

        trait Trait1 { val x: c.D }
      }
      package pkg2 {
        import e.F
        import g.H

        trait Trait2 { val y: g.H }
      }
      """

    doAnswer((pkg: Pkg) => pkg match {
      case aPkg if aPkg.structure == initialPkg1.structure => expectedFinalPkg1
      case aPkg if aPkg.structure == initialPkg2.structure => expectedFinalPkg2
      case aPkg => throw new IllegalStateException(s"No final Pkg has been stubbed for initial Pkg $aPkg")
    }).when(pkgImportAdder).addTo(any[Pkg])

    sourceImportAdder.addTo(initialSource).structure shouldBe expectedFinalSource.structure
  }
}
