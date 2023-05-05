package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.TypeTupleToTypeApplyTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteType

class TypeTupleTraverserImplTest extends UnitTestSuite {

  private val typeApplyTraverser = mock[TypeApplyTraverser]
  private val typeTupleToTypeApplyTransformer = mock[TypeTupleToTypeApplyTransformer]

  private val typeTupleTraverser = new TypeTupleTraverserImpl(typeApplyTraverser, typeTupleToTypeApplyTransformer)

  test("traverse") {
    val typeTuple = t"(T1, T2, T3)"
    val expectedTypeApply = t"Tuple3[T1, T2, T3]"
    val expectedTraversedTypeApply = t"Tuple3[U1, U2, U3]"

    when(typeTupleToTypeApplyTransformer.transform(eqTree(typeTuple))).thenReturn(expectedTypeApply)
    doReturn(expectedTraversedTypeApply).when(typeApplyTraverser).traverse(eqTree(expectedTypeApply))

    typeTupleTraverser.traverse(typeTuple).structure shouldBe expectedTraversedTypeApply.structure
  }

}
