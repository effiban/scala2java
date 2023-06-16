package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentContext, InitContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.XtensionQuasiquoteInit

class DeprecatedInitArgumentTraverserTest extends UnitTestSuite {

  private val initTraverser = mock[DeprecatedInitTraverser]
  private val initContext = mock[InitContext]
  private val argContext = mock[ArgumentContext]

  private val initArgumentTraverser = new DeprecatedInitArgumentTraverser(initTraverser, initContext)

  test("traverse()") {
    val init = init"MyType()"
    initArgumentTraverser.traverse(init, argContext)

    verify(initTraverser).traverse(eqTree(init), eqTo(initContext))
  }
}
