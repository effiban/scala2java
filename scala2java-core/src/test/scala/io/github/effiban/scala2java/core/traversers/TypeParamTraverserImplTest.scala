package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.{NameRenderer, TypeBoundsRenderer}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Type, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class TypeParamTraverserImplTest extends UnitTestSuite {

  private val nameTraverser = mock[NameTraverser]
  private val nameRenderer = mock[NameRenderer]
  private val typeParamListTraverser = mock[TypeParamListTraverser]
  private val typeBoundsTraverser = mock[TypeBoundsTraverser]
  private val typeBoundsRenderer = mock[TypeBoundsRenderer]

  private val typeParamTraverser = new TypeParamTraverserImpl(
    nameTraverser,
    nameRenderer,
    typeParamListTraverser,
    typeBoundsTraverser,
    typeBoundsRenderer
  )


  test("testTraverse") {
    val typeParam = tparam"T[K, V] <: Sortable"
    val typeBounds = Type.Bounds(lo = None, hi = Some(t"Sortable"))
    val traversedTypeBounds = Type.Bounds(lo = None, hi = Some(t"QuickSortable"))

    doReturn(t"T2").when(nameTraverser).traverse(eqTree(t"T"))
    doWrite("T2").when(nameRenderer).render(eqTree(t"T2"))
    doWrite("<K, V>").when(typeParamListTraverser).traverse(eqTreeList(List(tparam"K", tparam"V")))
    doReturn(traversedTypeBounds).when(typeBoundsTraverser).traverse(eqTree(typeBounds))
    doWrite(" extends QuickSortable").when(typeBoundsRenderer).render(eqTree(traversedTypeBounds))

    typeParamTraverser.traverse(typeParam)

    outputWriter.toString shouldBe "T2<K, V> extends QuickSortable"
  }

}
