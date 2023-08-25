package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Pkg, Source, XtensionQuasiquoteSource, XtensionQuasiquoteTerm}

class SourceQualifierImplTest extends UnitTestSuite {

  private val pkgQualifier = mock[PkgQualifier]

  private val sourceQualifier = new SourceQualifierImpl(pkgQualifier)


  test("qualify() when has no packages should return unchanged") {
    val source = Source(List(q"val x: Int = 3"))

    sourceQualifier.qualify(source).structure shouldBe source.structure
  }

  test("qualify() when has packages should return a Source containing the packages with nested qualified children") {
    val initialPkg1 =
      q"""
      package pkg1 {
        import a.A

        trait Trait1 { val x: Int }
      }
      """

    val initialPkg2 =
      q"""
      package pkg2 {
        import b.B

        trait Trait2 { val y: Double }
      }
      """

    val initialSource =
      source"""
      package pkg1 {
        import a.A

        trait Trait1 { val x: Int }
      }
      package pkg2 {
        import b.B

        trait Trait2 { val y: Double }
      }
      """

    val expectedFinalPkg1 =
      q"""
      package pkg1 {
        import a.A

        trait Trait1 { val x: scala.Int }
      }
      """

    val expectedFinalPkg2 =
      q"""
      package pkg2 {
        import b.B

        trait Trait2 { val y: scala.Double }
      }
      """

    val expectedFinalSource =
      source"""
      package pkg1 {
        import a.A

        trait Trait1 { val x: scala.Int }
      }
      package pkg2 {
        import b.B

        trait Trait2 { val y: scala.Double }
      }
      """

    doAnswer((pkg: Pkg) => pkg match {
      case aPkg if aPkg.structure == initialPkg1.structure => expectedFinalPkg1
      case aPkg if aPkg.structure == initialPkg2.structure => expectedFinalPkg2
      case aPkg => throw new IllegalStateException(s"No final Pkg has been stubbed for initial Pkg $aPkg")
    }).when(pkgQualifier).qualify(any[Pkg])

    sourceQualifier.qualify(initialSource).structure shouldBe expectedFinalSource.structure
  }
}
