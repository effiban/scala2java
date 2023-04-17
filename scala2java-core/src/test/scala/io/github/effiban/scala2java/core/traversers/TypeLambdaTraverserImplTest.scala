package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeLambdaRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Type, XtensionQuasiquoteType, XtensionQuasiquoteTypeParam}

class TypeLambdaTraverserImplTest extends UnitTestSuite {

  private val typeLambdaRenderer = mock[TypeLambdaRenderer]

  private val typeLambdaTraverser = new TypeLambdaTraverserImpl(typeLambdaRenderer)

  test("traverse") {
    val typeLambda = Type.Lambda(List(tparam"T1", tparam"T2"), t"U")

    typeLambdaTraverser.traverse(typeLambda)

    verify(typeLambdaRenderer).render(typeLambda)
  }

}
