package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Pkg, Source, XtensionQuasiquoteSource, XtensionQuasiquoteTerm}

class SourceImportRemoverImplTest extends UnitTestSuite {
  private val pkgImportRemover = mock[PkgImportRemover]

  private val sourceImportRemover = new SourceImportRemoverImpl(pkgImportRemover)

  test("removeUnusedFrom() when has no packages should return unchanged") {
    val source = Source(List(q"val x: Int = 3"))

    sourceImportRemover.removeUnusedFrom(source).structure shouldBe source.structure
  }

  test("removeUnusedFrom() when has packages should return a Source containing the packages with removed imports") {
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

        trait Trait1 { val x: c.D }
      }
      """

    val expectedFinalPkg2 =
      q"""
      package pkg2 {

        trait Trait2 { val y: g.H }
      }
      """

    val expectedFinalSource =
      source"""
      package pkg1 {

        trait Trait1 { val x: c.D }
      }
      package pkg2 {

        trait Trait2 { val y: g.H }
      }
      """

    doAnswer((pkg: Pkg) => pkg match {
      case aPkg if aPkg.structure == initialPkg1.structure => expectedFinalPkg1
      case aPkg if aPkg.structure == initialPkg2.structure => expectedFinalPkg2
      case aPkg => throw new IllegalStateException(s"No final Pkg has been stubbed for initial Pkg $aPkg")
    }).when(pkgImportRemover).removeUnusedFrom(any[Pkg])

    sourceImportRemover.removeUnusedFrom(initialSource).structure shouldBe expectedFinalSource.structure
  }

  test("removeJavaLangFrom() when has no packages should return unchanged") {
    val source = Source(List(q"val x: Int = 3"))

    sourceImportRemover.removeJavaLangFrom(source).structure shouldBe source.structure
  }

  test("removeJavaLangFrom() when has packages should return a Source containing the packages with removed imports") {
    val initialPkg1 =
      q"""
      package pkg1 {
        import java.lang.A

        trait Trait1 { val x: c.D }
      }
      """

    val initialPkg2 =
      q"""
      package pkg2 {
        import java.lang.B

        trait Trait2 { val y: g.H }
      }
      """

    val initialSource =
      source"""
      package pkg1 {
        import java.lang.A

        trait Trait1 { val x: c.D }
      }
      package pkg2 {
        import java.lang.B

        trait Trait2 { val y: g.H }
      }
      """

    val expectedFinalPkg1 =
      q"""
      package pkg1 {

        trait Trait1 { val x: c.D }
      }
      """

    val expectedFinalPkg2 =
      q"""
      package pkg2 {

        trait Trait2 { val y: g.H }
      }
      """

    val expectedFinalSource =
      source"""
      package pkg1 {

        trait Trait1 { val x: c.D }
      }
      package pkg2 {

        trait Trait2 { val y: g.H }
      }
      """

    doAnswer((pkg: Pkg) => pkg match {
      case aPkg if aPkg.structure == initialPkg1.structure => expectedFinalPkg1
      case aPkg if aPkg.structure == initialPkg2.structure => expectedFinalPkg2
      case aPkg => throw new IllegalStateException(s"No final Pkg has been stubbed for initial Pkg $aPkg")
    }).when(pkgImportRemover).removeJavaLangFrom(any[Pkg])

    sourceImportRemover.removeJavaLangFrom(initialSource).structure shouldBe expectedFinalSource.structure
  }
}
