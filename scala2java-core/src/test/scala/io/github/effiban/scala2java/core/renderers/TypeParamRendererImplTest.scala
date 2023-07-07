package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Type, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class TypeParamRendererImplTest extends UnitTestSuite {

  private val nameRenderer = mock[NameRenderer]
  private val typeParamListRenderer = mock[TypeParamListRenderer]
  private val typeBoundsRenderer = mock[TypeBoundsRenderer]

  private val typeParamRenderer = new TypeParamRendererImpl(
    nameRenderer,
    typeParamListRenderer,
    typeBoundsRenderer
  )


  test("render") {
    val typeParam = tparam"T[K, V] <: Sortable"
    val typeBounds = Type.Bounds(lo = None, hi = Some(t"Sortable"))

    doWrite("T").when(nameRenderer).render(eqTree(t"T"))
    doWrite("<K, V>").when(typeParamListRenderer).render(eqTreeList(List(tparam"K", tparam"V")))
    doWrite(" extends Sortable").when(typeBoundsRenderer).render(eqTree(typeBounds))

    typeParamRenderer.render(typeParam)

    outputWriter.toString shouldBe "T<K, V> extends Sortable"
  }

}
