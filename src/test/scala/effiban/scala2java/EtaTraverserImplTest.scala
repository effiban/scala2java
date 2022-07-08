package effiban.scala2java

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite

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
