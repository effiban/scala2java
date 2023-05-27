package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any

import scala.meta.{Tree, XtensionQuasiquoteTerm}

class StatDesugarerImplTest extends UnitTestSuite {

  private val pkgDesugarer = mock[PkgDesugarer]
  private val defnDesugarer = mock[DefnDesugarer]
  private val declDesugarer = mock[DeclDesugarer]
  private val evaluatedTermDesugarer = mock[EvaluatedTermDesugarer]
  private val treeDesugarer = mock[TreeDesugarer]

  private val statDesugarer = new StatDesugarerImpl(
    pkgDesugarer,
    defnDesugarer,
    declDesugarer,
    evaluatedTermDesugarer,
    treeDesugarer
  )

  test("desugar Pkg") {
    val pkg =
      q"""
      package a.b {
        object C {
          val x = func
        }
      }
      """

    val desugaredPkg =
      q"""
      package a.b {
        object C {
          val x = func()
        }
      }
      """

    doReturn(desugaredPkg).when(pkgDesugarer).desugar(eqTree(pkg))

    statDesugarer.desugar(pkg).structure shouldBe desugaredPkg.structure
  }

  test("desugar Defn") {
    val defn = q"val x = calc"
    val desugaredDefn = q"val x = calc()"

    doReturn(desugaredDefn).when(defnDesugarer).desugar(eqTree(defn))

    statDesugarer.desugar(defn).structure shouldBe desugaredDefn.structure
  }

  test("desugar Decl") {
    val decl = q"def foo(x: Int = calc): Unit"
    val desugaredDecl = q"def foo(x: Int = calc()): Unit"

    doReturn(desugaredDecl).when(declDesugarer).desugar(eqTree(decl))

    statDesugarer.desugar(decl).structure shouldBe desugaredDecl.structure
  }

  test("desugar Term") {
    val term = q"func"
    val desugaredTerm = q"func()"

    doReturn(desugaredTerm).when(evaluatedTermDesugarer).desugar(term)

    statDesugarer.desugar(term).structure shouldBe desugaredTerm.structure
  }

  test("desugar Ctor.Secondary") {
    val secondaryCtor = q"""
    def this(x: Int) = {
      this()
      func
    }
    """

    val desugaredSecondaryCtor =
      q"""
      def this(x: Int) = {
      this()
      func()
      }
      """

    def func = q"func"
    def desugaredFunc = q"func()"

    when(treeDesugarer.desugar(any())).thenAnswer((t: Tree) => t)
    doReturn(desugaredFunc).when(evaluatedTermDesugarer).desugar(eqTree(func))

    statDesugarer.desugar(secondaryCtor).structure shouldBe desugaredSecondaryCtor.structure
  }


  test("desugar Import") {
    val `import` = q"import a.b.c"

    statDesugarer.desugar(`import`).structure shouldBe `import`.structure
  }
}
