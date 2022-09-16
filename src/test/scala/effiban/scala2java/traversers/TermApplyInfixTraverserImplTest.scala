package effiban.scala2java.traversers

import effiban.scala2java.classifiers.TermApplyInfixClassifier
import effiban.scala2java.entities.ListTraversalOptions
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TermNames.{PlusTermName, ScalaRangeTermName, ScalaToTermName}
import effiban.scala2java.transformers.TermApplyInfixToRangeTransformer
import org.mockito.ArgumentMatchers

import scala.meta.Term

class TermApplyInfixTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]
  private val termApplyTraverser = mock[TermApplyTraverser]
  private val termNameTraverser = mock[TermNameTraverser]
  private val termListTraverser = mock[TermListTraverser]
  private val termApplyInfixClassifier = mock[TermApplyInfixClassifier]
  private val termApplyInfixToRangeTransformer = mock[TermApplyInfixToRangeTransformer]

  private val termApplyInfixTraverser = new TermApplyInfixTraverserImpl(
    termTraverser,
    termApplyTraverser,
    termNameTraverser,
    termListTraverser,
    termApplyInfixClassifier,
    termApplyInfixToRangeTransformer)

  test("traverse() when has range operator") {
    val lhs = Term.Name("a")
    val rhs = Term.Name("b")

    val applyInfix = Term.ApplyInfix(
      lhs = lhs,
      op = ScalaToTermName,
      targs = Nil,
      args = List(rhs)
    )

    val expectedRangeTermApply = Term.Apply(fun = ScalaRangeTermName, args = List(lhs, rhs))

    when(termApplyInfixClassifier.isRange(eqTree(applyInfix))).thenReturn(true)
    when(termApplyInfixToRangeTransformer.transform(eqTree(applyInfix))).thenReturn(expectedRangeTermApply)

    termApplyInfixTraverser.traverse(applyInfix)

    verify(termApplyTraverser).traverse(eqTree(expectedRangeTermApply))
  }

  test("traverse() when has arithmetic operator") {
    val lhs = Term.Name("a")
    val op = PlusTermName
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
      ArgumentMatchers.eq(ListTraversalOptions(onSameLine = true))
    )

    termApplyInfixTraverser.traverse(applyInfix)

    outputWriter.toString shouldBe "a + b"
  }

}
