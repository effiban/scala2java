package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentContext, StatContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteTermParam

class DeprecatedTermParamArgumentTraverserTest extends UnitTestSuite {

  private val termParamTraverser = mock[DeprecatedTermParamTraverser]
  private val statContext = mock[StatContext]
  private val argContext = mock[ArgumentContext]

  private val termParamArgumentTraverser = new DeprecatedTermParamArgumentTraverser(termParamTraverser, statContext)

  test("traverse()") {
    val termParam = param"x: Int"

    termParamArgumentTraverser.traverse(termParam, argContext)

    verify(termParamTraverser).traverse(eqTree(termParam), eqTo(statContext))
  }
}
