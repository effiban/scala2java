package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Type, XtensionQuasiquoteType}

class TypeFunctionTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]

  private val typeFunctionTraverser = new TypeFunctionTraverserImpl(typeTraverser)

  test("traverse()") {
    val functionType = t"(T1, T2) => T3"
    val expectedTraversedFunctionType = t"(U1, U2) => U3"

    doAnswer((tpe: Type) => tpe match {
      case t"T1" => t"U1"
      case t"T2" => t"U2"
      case t"T3" => t"U3"
      case other => other
    }).when(typeTraverser).traverse(any[Type])

    typeFunctionTraverser.traverse(functionType).structure shouldBe expectedTraversedFunctionType.structure
  }
}
