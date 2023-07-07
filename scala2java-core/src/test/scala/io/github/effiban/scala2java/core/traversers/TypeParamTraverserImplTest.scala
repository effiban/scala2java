package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeBounds
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Name, Type, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class TypeParamTraverserImplTest extends UnitTestSuite {

  private val nameTraverser = mock[NameTraverser]
  private val typeBoundsTraverser = mock[TypeBoundsTraverser]

  private val typeParamTraverser = new TypeParamTraverserImpl(nameTraverser, typeBoundsTraverser)


  test("traverse with nested type params and bounds") {
    val typeParam = tparam"T[K, V] <: Sortable"
    val traversedTypeParam = tparam"T2[K2, V2] <: QuickSortable"

    val theTypeName = t"T"
    val theTraversedTypeName = t"T2"
    val nestedTypeName1 = t"K"
    val traversedNestedTypeName1 = t"K2"
    val nestedTypeName2 = t"V"
    val traversedNestedTypeName2 = t"V2"

    val theTypeBounds = Type.Bounds(lo = None, hi = Some(t"Sortable"))
    val theTraversedTypeBounds = Type.Bounds(lo = None, hi = Some(t"QuickSortable"))

    doAnswer((name: Name) => name match {
      case aName if aName.structure == theTypeName.structure => theTraversedTypeName
      case aName if aName.structure == nestedTypeName1.structure => traversedNestedTypeName1
      case aName if aName.structure == nestedTypeName2.structure => traversedNestedTypeName2
      case aName => aName
    }).when(nameTraverser).traverse(any[Name])

    doAnswer((typeBounds: Type.Bounds) => typeBounds match {
      case tBounds if tBounds.structure == theTypeBounds.structure => theTraversedTypeBounds
      case tBounds => tBounds
    }).when(typeBoundsTraverser).traverse(any[Type.Bounds])

    typeParamTraverser.traverse(typeParam).structure shouldBe traversedTypeParam.structure
  }

  test("traverse with name only") {
    val typeParam = tparam"T"
    val traversedTypeParam = tparam"T2"

    val theTypeName = t"T"
    val theTraversedTypeName = t"T2"

    doReturn(theTraversedTypeName).when(nameTraverser).traverse(eqTree(theTypeName))
    doReturn(TypeBounds.Empty).when(typeBoundsTraverser).traverse(eqTree(TypeBounds.Empty))

    typeParamTraverser.traverse(typeParam).structure shouldBe traversedTypeParam.structure
  }
}
