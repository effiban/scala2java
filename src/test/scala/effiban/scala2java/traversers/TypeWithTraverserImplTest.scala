package effiban.scala2java.traversers

import effiban.scala2java.UnitTestSuite
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite

import scala.meta.Type

class TypeWithTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]

  private val typeWithTraverser = new TypeWithTraverserImpl(typeTraverser)

  test("traverse") {
    val lhs = Type.Name("A")
    val rhs = Type.Name("B")
    val typeWith = Type.With(lhs, rhs)

    doWrite("A").when(typeTraverser).traverse(eqTree(lhs))
    doWrite("B").when(typeTraverser).traverse(eqTree(rhs))

    typeWithTraverser.traverse(typeWith)

    outputWriter.toString shouldBe "A extends B"
  }

}
