package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Term
import scala.meta.Term.Return

class ReturnTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]

  private val returnTraverser = new ReturnTraverserImpl(termTraverser)

  test("traverse()") {
    val x = Term.Name("x")

    doWrite("x").when(termTraverser).traverse(eqTree(x))

    returnTraverser.traverse(Return(x))

    outputWriter.toString shouldBe "return x"
  }
}
