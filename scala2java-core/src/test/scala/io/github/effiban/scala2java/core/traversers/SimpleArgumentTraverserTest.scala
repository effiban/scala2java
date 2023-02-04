package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Tree, XtensionQuasiquoteTerm}

class SimpleArgumentTraverserTest extends UnitTestSuite {

  private val treeTraverser = mock[ScalaTreeTraverser[Tree]]
  private val argContext = mock[ArgumentContext]

  private val simpleArgumentTraverser = new SimpleArgumentTraverser(treeTraverser)

  test("traverse()") {
    val arg = q"dummy"

    simpleArgumentTraverser.traverse(arg, argContext)

    verify(treeTraverser).traverse(arg)
  }
}
