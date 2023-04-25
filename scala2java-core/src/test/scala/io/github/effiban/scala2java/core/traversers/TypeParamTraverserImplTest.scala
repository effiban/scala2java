package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.NameRenderer
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

  private val typeParamTraverser = new TypeParamTraverserImpl(
    nameTraverser,
    nameRenderer,
    typeParamListTraverser,
    typeBoundsTraverser
  )


  test("testTraverse") {
    val typeParam = tparam"T[K, V] <: Sortable"

    doReturn(t"T2").when(nameTraverser).traverse(eqTree(t"T"))
    doWrite("T2").when(nameRenderer).render(eqTree(t"T2"))
    doWrite("<K, V>").when(typeParamListTraverser).traverse(eqTreeList(List(tparam"K", tparam"V")))
    doWrite(" extends Sortable").when(typeBoundsTraverser).traverse(eqTree(Type.Bounds(lo = None, hi = Some(t"Sortable"))))

    typeParamTraverser.traverse(typeParam)

    outputWriter.toString shouldBe "T2<K, V> extends Sortable"
  }

}
