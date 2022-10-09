package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Term
import scala.meta.Term.Eta

class EtaTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]

  private val etaTraverser = new EtaTraverserImpl(termTraverser)

  test("traverse()") {
    val methodName = Term.Name("myMethod")

    doWrite("myMethod").when(termTraverser).traverse(eqTree(methodName))

    etaTraverser.traverse(Eta(methodName))

    outputWriter.toString shouldBe "this::myMethod"
  }

}
