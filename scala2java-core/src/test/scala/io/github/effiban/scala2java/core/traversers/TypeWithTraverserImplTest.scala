package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

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
