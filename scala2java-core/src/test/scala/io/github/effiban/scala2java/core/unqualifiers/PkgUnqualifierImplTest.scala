package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.importmanipulation.StatsByImportSplitter
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.any
import org.mockito.Mockito.verifyNoInteractions

import scala.meta.{Importer, Term, Type, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class PkgUnqualifierImplTest extends UnitTestSuite {

  private val statsByImportSplitter = mock[StatsByImportSplitter]
  private val termSelectUnqualifier = mock[TermSelectUnqualifier]
  private val typeSelectUnqualifier = mock[TypeSelectUnqualifier]
  private val typeProjectUnqualifier = mock[TypeProjectUnqualifier]

  private val pkgUnqualifier = new PkgUnqualifierImpl(
    statsByImportSplitter,
    termSelectUnqualifier,
    typeSelectUnqualifier,
    typeProjectUnqualifier
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

    verifyNoInteractions(termSelectUnqualifier, typeSelectUnqualifier)
  }

  test("unqualify when has nested Term.Select-s but TermSelectUnqualifier returns unchanged, should return unchanged") {
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
    doAnswer((termSelect: Term.Select) => termSelect).when(termSelectUnqualifier).unqualify(any[Term.Select], eqTreeList(expectedImporters))

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

  test("unqualify when has nested Type.Project-s but TypeProjectUnqualifier returns unchanged, should return unchanged") {
    val pkg =
      q"""
      package a.b {
        import c.C
        import d.D

        trait MyTrait1 {
          val e: E#F
        }
        trait MyTrait2 {
          val g: G#H
        }
      }
      """

    val expectedImporters = List(importer"c.C", importer"d.D")
    val expectedNonImports = List(
      q"""
      trait MyTrait1 {
        val e: E#F
      }
      """,
      q"""
      trait MyTrait2 {
        val g: G#H
      }
      """
    )
    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(pkg.stats))
    doAnswer((typeProject: Type.Project) => typeProject).when(typeProjectUnqualifier).unqualify(any[Type.Project], eqTreeList(expectedImporters))

    pkgUnqualifier.unqualify(pkg).structure shouldBe pkg.structure
  }

  test("unqualify when TermSelectUnqualifier unqualifies some of the Term.Select-s by full match, should return them unqualified") {
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

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termSelect: Term.Select, _: List[Importer]) => termSelect match {
      case aTermSelect if aTermSelect.structure == q"c.c1".structure => q"c1"
      case aTermSelect if aTermSelect.structure == q"d.d1".structure => q"d1"
      case aTermSelect => aTermSelect
    }).when(termSelectUnqualifier).unqualify(any[Term.Select], eqTreeList(expectedImporters))

    pkgUnqualifier.unqualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("unqualify when TermSelectUnqualifier unqualifies some Term.Select-s by prefix match, should return them unqualified") {
    val initialPkg =
      q"""
    package a.b {
      import c.c1.c2
      import d.d1.d2

      trait MyTrait {
        def foo(): Unit = {
          c1.c2.c3()
          d1.d2.d3()
        }
      }
    }
    """

    val expectedImporters = List(importer"c.c1.c2", importer"d.d1.d2")
    val expectedNonImports = List(
      q"""
    trait MyTrait {
              def foo(): Unit = {
                c1.c2.c3()
                d1.d2.d3()
              }
            }
    """
    )

    val expectedFinalPkg =
      q"""
    package a.b {
      import c.c1.c2
      import d.d1.d2

      trait MyTrait {
        def foo(): Unit = {
          c2.c3()
          d2.d3()
        }
      }
    }
    """

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termSelect: Term.Select, _: List[Importer]) => termSelect match {
      case aTermSelect if aTermSelect.structure == q"c1.c2".structure => q"c2"
      case aTermSelect if aTermSelect.structure == q"d1.d2".structure => q"d2"
      case aTermSelect => aTermSelect
    }).when(termSelectUnqualifier).unqualify(any[Term.Select], eqTreeList(expectedImporters))

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

    val expectedImporters = List(importer"c.C", importer"d.D")
    val expectedNonImports = List(
      q"""
      trait MyTrait {
        def doC(): c.C
        def doD(): d.D
      }
      """
    )

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

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termSelect: Term.Select) => termSelect).when(termSelectUnqualifier).unqualify(any[Term.Select], eqTreeList(expectedImporters))
    doAnswer((typeSelect: Type.Select, _: List[Importer]) => typeSelect match {
      case aTypeSelect if aTypeSelect.structure == t"c.C".structure => t"C"
      case aTypeSelect if aTypeSelect.structure == t"d.D".structure => t"D"
      case aTypeSelect => aTypeSelect
    }).when(typeSelectUnqualifier).unqualify(any[Type.Select], eqTreeList(expectedImporters))

    pkgUnqualifier.unqualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("unqualify when TypeProjectUnqualifier unqualifies some of the Type.Projects by full match, should return them unqualified") {
    val initialPkg =
      q"""
      package a.b {
        import C.D
        import E.F

        trait MyTrait {
          def f1(): C#D
          def f2(): E#F
        }
      }
      """

    val expectedImporters = List(importer"C.D", importer"E.F")
    val expectedNonImports = List(
      q"""
      trait MyTrait {
        def f1(): C#D
        def f2(): E#F
      }
      """
    )

    val expectedFinalPkg =
      q"""
      package a.b {
        import C.D
        import E.F

        trait MyTrait {
          def f1(): D
          def f2(): F
        }
      }
      """

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termSelect: Term.Select) => termSelect).when(termSelectUnqualifier).unqualify(any[Term.Select], eqTreeList(expectedImporters))
    doAnswer((typeProject: Type.Project, _: List[Importer]) => typeProject match {
      case aTypeProject if aTypeProject.structure == t"C#D".structure => t"D"
      case aTypeProject if aTypeProject.structure == t"E#F".structure => t"F"
      case aTypeProject => aTypeProject
    }).when(typeProjectUnqualifier).unqualify(any[Type.Project], eqTreeList(expectedImporters))

    pkgUnqualifier.unqualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }

  test("unqualify when TypeProjectUnqualifier unqualifies some of the Type.Projects by partial match, should return them unqualified") {
    val initialPkg =
      q"""
      package a.b {
        import C.C1.C2
        import D.D1.D2

        trait MyTrait {
          def f1(): C1#C2#C3
          def f2(): D1#D2#D3
        }
      }
      """

    val expectedImporters = List(importer"C.C1.C2", importer"D.D1.D2")
    val expectedNonImports = List(
      q"""
      trait MyTrait {
        def f1(): C1#C2#C3
        def f2(): D1#D2#D3
      }
      """
    )

    val expectedFinalPkg =
      q"""
      package a.b {
        import C.C1.C2
        import D.D1.D2

        trait MyTrait {
          def f1(): C2#C3
          def f2(): D2#D3
        }
      }
      """

    doReturn((expectedImporters, expectedNonImports)).when(statsByImportSplitter).split(eqTreeList(initialPkg.stats))
    doAnswer((termSelect: Term.Select) => termSelect).when(termSelectUnqualifier).unqualify(any[Term.Select], eqTreeList(expectedImporters))
    doAnswer((typeProject: Type.Project, _: List[Importer]) => typeProject match {
      case aTypeProject if aTypeProject.structure == t"C1#C2".structure => t"C2"
      case aTypeProject if aTypeProject.structure == t"D1#D2".structure => t"D2"
      case aTypeProject => aTypeProject
    }).when(typeProjectUnqualifier).unqualify(any[Type.Project], eqTreeList(expectedImporters))

    pkgUnqualifier.unqualify(initialPkg).structure shouldBe expectedFinalPkg.structure
  }
}
