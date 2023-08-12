package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Pkg, Source, XtensionQuasiquoteSource, XtensionQuasiquoteTerm}

class SourceUnqualifierImplTest extends UnitTestSuite {

  private val pkgUnqualifier = mock[PkgUnqualifier]

  private val sourceUnqualifier = new SourceUnqualifierImpl(pkgUnqualifier)

  
  test("unqualify() when has no packages should return unchanged") {
    val source = Source(List(q"val x: Int = 3"))

    sourceUnqualifier.unqualify(source).structure shouldBe source.structure
  }

  test("unqualify() when has packages should return a Source containing the packages with nested unqualified children") {
    val initialPkg1 =
      q"""
      package pkg1 {
        import a.A

        trait Trait1 { val x: a.A }
      }
      """

    val initialPkg2 =
      q"""
      package pkg2 {
        import b.B

        trait Trait2 { val y: b.B }
      }
      """

    val initialSource =
      source"""
      package pkg1 {
        import a.A

        trait Trait1 { val x: a.A }
      }
      package pkg2 {
        import b.B

        trait Trait2 { val y: b.B }
      }
      """

    val expectedFinalPkg1 =
      q"""
      package pkg1 {
        import a.A

        trait Trait1 { val x: A }
      }
      """

    val expectedFinalPkg2 =
      q"""
      package pkg2 {
        import b.B

        trait Trait2 { val y: B }
      }
      """

    val expectedFinalSource =
      source"""
      package pkg1 {
        import a.A

        trait Trait1 { val x: A }
      }
      package pkg2 {
        import b.B

        trait Trait2 { val y: B }
      }
      """

    doAnswer((pkg: Pkg) => pkg match {
      case aPkg if aPkg.structure == initialPkg1.structure => expectedFinalPkg1
      case aPkg if aPkg.structure == initialPkg2.structure => expectedFinalPkg2
      case aPkg => throw new IllegalStateException(s"No final Pkg has been stubbed for initial Pkg $aPkg")
    }).when(pkgUnqualifier).unqualify(any[Pkg])

    sourceUnqualifier.unqualify(initialSource).structure shouldBe expectedFinalSource.structure
  }

}
