package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentContext, StatContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTermParam

class TermParamArgumentTraverserTest extends UnitTestSuite {

  private val termParamTraverser = mock[TermParamTraverser]
  private val statContext = mock[StatContext]
  private val argContext = mock[ArgumentContext]

  private val termParamArgumentTraverser = new TermParamArgumentTraverser(termParamTraverser, statContext)

  test("traverse()") {
    val termParam = param"x: Int"

    termParamArgumentTraverser.traverse(termParam, argContext)

    verify(termParamTraverser).traverse(eqTree(termParam), eqTo(statContext))
  }
}
