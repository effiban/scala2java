package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.core.transformers.TypeTupleToTypeApplyTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Type

class TypeTupleTraverserImplTest extends UnitTestSuite {

  private val typeApplyTraverser = mock[TypeApplyTraverser]
  private val typeTupleToTypeApplyTransformer = mock[TypeTupleToTypeApplyTransformer]

  private val typeTupleTraverser = new TypeTupleTraverserImpl(typeApplyTraverser, typeTupleToTypeApplyTransformer)

  test("traverse") {
    val types = List(TypeNames.String, TypeNames.Int, TypeNames.Double)
    val typeTuple = Type.Tuple(types)
    val expectedTypeApply = Type.Apply(Type.Name("Tuple3"), types)

    when(typeTupleToTypeApplyTransformer.transform(eqTree(typeTuple))).thenReturn(expectedTypeApply)

    typeTupleTraverser.traverse(typeTuple)

    verify(typeApplyTraverser).traverse(eqTree(expectedTypeApply))
  }

}
