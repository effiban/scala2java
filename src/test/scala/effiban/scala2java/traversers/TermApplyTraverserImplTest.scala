package effiban.scala2java.traversers

import effiban.scala2java.contexts.TermContext
import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.entities.ListTraversalOptions
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.transformers.ScalaToJavaTermApplyTransformer
import org.mockito.ArgumentMatchers

import scala.meta.Term

class TermApplyTraverserImplTest extends UnitTestSuite {
  private val termTraverser = mock[TermTraverser]
  private val termListTraverser = mock[TermListTraverser]
  private val scalaToJavaTermApplyTransformer = mock[ScalaToJavaTermApplyTransformer]

  private val termApplyTraverser = new TermApplyTraverserImpl(
    termTraverser,
    termListTraverser,
    scalaToJavaTermApplyTransformer
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

    when(scalaToJavaTermApplyTransformer.transform(eqTree(scalaTermApply))).thenReturn(javaTermApply)

    doWrite("myJavaMethod").when(termTraverser).traverse(eqTree(javaTermApply.fun), ArgumentMatchers.eq(TermContext()))
    doWrite("(arg1, arg2)").when(termListTraverser).traverse(
      terms = eqTreeList(javaTermApply.args),
      ArgumentMatchers.eq(ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses), traverseEmpty = true)),
      ArgumentMatchers.eq(TermContext())
    )

    termApplyTraverser.traverse(scalaTermApply)

    outputWriter.toString shouldBe "myJavaMethod(arg1, arg2)"

  }

}
