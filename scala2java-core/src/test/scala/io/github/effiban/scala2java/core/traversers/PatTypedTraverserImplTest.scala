package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Pat, Term, Type}

class PatTypedTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val patTraverser = mock[PatTraverser]

  val patTypedTraverser = new PatTypedTraverserImpl(typeTraverser, patTraverser)

  test("traverse()") {
    val lhs = Pat.Var(Term.Name("x"))
    val rhs = Type.Name("MyType")

    doWrite("MyType").when(typeTraverser).traverse(eqTree(rhs))
    doWrite("x").when(patTraverser).traverse(eqTree(lhs))

    patTypedTraverser.traverse(Pat.Typed(lhs = lhs, rhs = rhs))

    outputWriter.toString shouldBe "MyType x"
  }
}
