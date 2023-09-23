package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Pkg, XtensionQuasiquoteTerm}

class PkgTraverserImplTest extends UnitTestSuite {

  private val Import1 = q"import pkg1.Class1"
  private val Import2 = q"import pkg2.Class2"

  private val TheClass = q"class MyClass { def foo(x: Int) = x + 1 }"
  private val TheTraversedClass = q"class MyTraversedClass { def foo(xx: Int) = xx + 1 }"

  private val defaultTermRefTraverser = mock[DefaultTermRefTraverser]
  private val pkgStatListTraverser = mock[PkgStatListTraverser]

  private val pkgTraverser = new PkgTraverserImpl(
    defaultTermRefTraverser,
    pkgStatListTraverser
  )


  test("traverse()") {
    val pkgRef = q"mypkg.myinnerpkg"
    val traversedPkgRef = q"mytraversedpkg.myinnerpkg"

    val stats = List(Import1, Import2, TheClass)
    val pkg = Pkg(pkgRef, stats)
    val expectedTraversedStats = List(
      Import1,
      Import2,
      TheTraversedClass
    )

    val expectedPkg = Pkg(ref = traversedPkgRef, stats = expectedTraversedStats)

    doReturn(traversedPkgRef).when(defaultTermRefTraverser).traverse(eqTree(pkgRef))
    doReturn(expectedTraversedStats).when(pkgStatListTraverser).traverse(eqTreeList(stats))

    pkgTraverser.traverse(pkg).structure shouldBe expectedPkg.structure
  }
}
