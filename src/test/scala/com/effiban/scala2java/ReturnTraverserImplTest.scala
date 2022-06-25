package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeMatcher.eqTree
import com.effiban.scala2java.stubbers.OutputWriterStubber.doWrite

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
