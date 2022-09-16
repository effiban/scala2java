package effiban.scala2java.traversers

import effiban.scala2java.contexts.TermContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.Term

class ApplyUnaryTraverserImplTest extends UnitTestSuite {

  private val termNameTraverser = mock[TermNameTraverser]
  private val termTraverser = mock[TermTraverser]

  private val applyUnaryTraverser = new ApplyUnaryTraverserImpl(termNameTraverser, termTraverser)

  test("traverse") {
    val op = Term.Name("!")
    val arg = Term.Name("myFlag")

    doWrite("!").when(termNameTraverser).traverse(eqTree(op))
    doWrite("myFlag").when(termTraverser).traverse(eqTree(arg), ArgumentMatchers.eq(TermContext()))

    applyUnaryTraverser.traverse(Term.ApplyUnary(op = op, arg = arg))

    outputWriter.toString shouldBe "!myFlag"
  }
}
