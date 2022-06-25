package com.effiban.scala2java

import com.effiban.scala2java.matchers.SomeMatcher.eqSome
import com.effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import com.effiban.scala2java.matchers.TreeMatcher.eqTree
import com.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import org.mockito.ArgumentMatchers

import scala.meta.Term

class TermApplyTraverserImplTest extends UnitTestSuite {
  private val termTraverser = mock[TermTraverser]
  private val termListTraverser = mock[TermListTraverser]

  private val termApplyTraverser = new TermApplyTraverserImpl(termTraverser, termListTraverser)

  test("traverse") {
    val termApply = Term.Apply(
      fun = Term.Name("myMethod"),
      args = List(Term.Name("arg1"), Term.Name("arg2"))
    )

    doWrite("myMethod").when(termTraverser).traverse(eqTree(termApply.fun))
    doWrite("(arg1, arg2)").when(termListTraverser).traverse(
      terms = eqTreeList(termApply.args),
      onSameLine = ArgumentMatchers.eq(false),
      maybeDelimiterType = eqSome(Parentheses))

    termApplyTraverser.traverse(termApply)

    outputWriter.toString shouldBe "myMethod(arg1, arg2)"

  }

}
