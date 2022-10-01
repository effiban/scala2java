package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import effiban.scala2java.transformers.TypeTupleToTypeApplyTransformer

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
