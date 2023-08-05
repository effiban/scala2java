package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.entities.{JavaModifier, SealedHierarchies}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results._
import io.github.effiban.scala2java.spi.providers.AdditionalImportersProvider
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Import, Pkg, XtensionQuasiquoteImporter, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class PkgTraverserImplTest extends UnitTestSuite {

  private val ArbitraryImport = q"import extpkg.ExtClass"

  private val CoreImporters = List(
    importer"java.lang._",
    importer"java.util._"
  )

  private val TheClass =
    q"""
    class MyClass {
      def foo(x: Int) = x + 1
    }
    """
  private val TheClassTraversalResult = TestableRegularClassTraversalResult(
    defnClass = TheClass,
    javaModifiers = List(JavaModifier.Public),
    statResults = List(DefnDefTraversalResult(q"def traversedFoo(xx: Int) = xx + 1"))
  )

  private val TheSealedHierarchies = SealedHierarchies(
    Map(t"Parent" -> List(TheClass.name))
  )

  private val defaultTermRefTraverser = mock[DefaultTermRefTraverser]
  private val pkgStatListTraverser = mock[PkgStatListTraverser]
  private val additionalImportersProvider = mock[AdditionalImportersProvider]

  private val pkgTraverser = new PkgTraverserImpl(
    defaultTermRefTraverser,
    pkgStatListTraverser,
    additionalImportersProvider
  )


  test("traverse()") {
    val pkgRef = q"mypkg.myinnerpkg"
    val traversedPkgRef = q"mytraversedpkg.myinnerpkg"

    val stats = List(ArbitraryImport, TheClass)
    val pkg = Pkg(pkgRef, stats)
    val expectedEnrichedStats = Import(CoreImporters) +: stats
    val expectedStatResults = List(
      SimpleStatTraversalResult(ArbitraryImport),
      SimpleStatTraversalResult(Import(CoreImporters)),
      TheClassTraversalResult
    )

    val expectedPkgStatListTraversalResult = PkgStatListTraversalResult(
      statResults = expectedStatResults,
      sealedHierarchies = TheSealedHierarchies
    )
    val expectedPkg = Pkg(
      ref = traversedPkgRef,
      stats = expectedStatResults.map(_.tree),
    )

    doReturn(traversedPkgRef).when(defaultTermRefTraverser).traverse(eqTree(pkgRef))
    when(additionalImportersProvider.provide()).thenReturn(CoreImporters)
    doReturn(expectedPkgStatListTraversalResult).when(pkgStatListTraverser).traverse(eqTreeList(expectedEnrichedStats))

    pkgTraverser.traverse(pkg).structure shouldBe expectedPkg.structure
  }
}
