package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeBounds
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Type, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class TypeParamTraverserImplTest extends UnitTestSuite {

  private val typeBoundsTraverser = mock[TypeBoundsTraverser]

  private val typeParamTraverser = new TypeParamTraverserImpl(typeBoundsTraverser)


  test("traverse with nested type params and bounds") {
    val typeParam = tparam"T[K, V] <: Sortable"
    val traversedTypeParam = tparam"T[K, V] <: QuickSortable"

    val theTypeBounds = Type.Bounds(lo = None, hi = Some(t"Sortable"))
    val theTraversedTypeBounds = Type.Bounds(lo = None, hi = Some(t"QuickSortable"))

    doAnswer((typeBounds: Type.Bounds) => typeBounds match {
      case tBounds if tBounds.structure == theTypeBounds.structure => theTraversedTypeBounds
      case tBounds => tBounds
    }).when(typeBoundsTraverser).traverse(any[Type.Bounds])

    typeParamTraverser.traverse(typeParam).structure shouldBe traversedTypeParam.structure
  }

  test("traverse with name only") {
    val typeParam = tparam"T"

    doReturn(TypeBounds.Empty).when(typeBoundsTraverser).traverse(eqTree(TypeBounds.Empty))

    typeParamTraverser.traverse(typeParam).structure shouldBe typeParam.structure
  }
}
