package effiban.scala2java.traversers

import effiban.scala2java.UnitTestSuite
import effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import org.mockito.ArgumentMatchers

import scala.meta.Term

class TermApplyInfixTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]
  private val termNameTraverser = mock[TermNameTraverser]
  private val termListTraverser = mock[TermListTraverser]

  private val termApplyInfixTraverser = new TermApplyInfixTraverserImpl(termTraverser, termNameTraverser, termListTraverser)

  test("traverse") {
    val lhs = Term.Name("a")
    val op = Term.Name("+")
    val rhs = Term.Name("b")

    val applyInfix = Term.ApplyInfix(
      lhs = lhs,
      op = op,
      targs = Nil,
      args = List(rhs)
    )

    doWrite("a").when(termTraverser).traverse(eqTree(lhs))
    doWrite("+").when(termNameTraverser).traverse(eqTree(op))
    doWrite("b").when(termListTraverser).traverse(
      terms = eqTreeList(List(rhs)),
      onSameLine = ArgumentMatchers.eq(true),
      maybeDelimiterType = ArgumentMatchers.eq(None)
    )

    termApplyInfixTraverser.traverse(applyInfix)

    outputWriter.toString shouldBe "a + b"
  }

}
