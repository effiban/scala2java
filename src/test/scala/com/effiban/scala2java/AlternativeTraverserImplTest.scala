package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeMatcher.eqTree
import com.effiban.scala2java.stubbers.OutputWriterStubber.doWrite

import scala.meta.{Lit, Pat}

class AlternativeTraverserImplTest extends UnitTestSuite {

  private val patTraverser = mock[PatTraverser]

  private val alternativeTraverser = new AlternativeTraverserImpl(patTraverser)

  test("traverse") {
    val lhs = Lit.Int(3)
    val rhs = Lit.Int(4)

    doWrite("3").when(patTraverser).traverse(eqTree(lhs))
    doWrite("4").when(patTraverser).traverse(eqTree(rhs))

    alternativeTraverser.traverse(Pat.Alternative(lhs, rhs))

    outputWriter.toString shouldBe "3, 4"
  }
}
