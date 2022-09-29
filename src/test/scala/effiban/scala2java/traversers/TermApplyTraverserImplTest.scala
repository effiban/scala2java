package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.entities.ListTraversalOptions
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.transformers.TermApplyTransformer
import org.mockito.ArgumentMatchers

import scala.meta.Term

class TermApplyTraverserImplTest extends UnitTestSuite {
  private val termTraverser = mock[TermTraverser]
  private val termListTraverser = mock[TermListTraverser]
  private val termApplyTransformer = mock[TermApplyTransformer]

  private val termApplyTraverser = new TermApplyTraverserImpl(
    termTraverser,
    termListTraverser,
    termApplyTransformer
  )

  test("traverse") {
    val scalaTermApply = Term.Apply(
      fun = Term.Name("myMethod"),
      args = List(Term.Name("arg1"), Term.Name("arg2"))
    )
    val javaTermApply = Term.Apply(
      fun = Term.Name("myJavaMethod"),
      args = List(Term.Name("arg1"), Term.Name("arg2"))
    )

    when(termApplyTransformer.transform(eqTree(scalaTermApply))).thenReturn(javaTermApply)

    doWrite("myJavaMethod").when(termTraverser).traverse(eqTree(javaTermApply.fun))
    doWrite("(arg1, arg2)").when(termListTraverser).traverse(
      terms = eqTreeList(javaTermApply.args),
      ArgumentMatchers.eq(ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses), traverseEmpty = true))
    )

    termApplyTraverser.traverse(scalaTermApply)

    outputWriter.toString shouldBe "myJavaMethod(arg1, arg2)"

  }

}
