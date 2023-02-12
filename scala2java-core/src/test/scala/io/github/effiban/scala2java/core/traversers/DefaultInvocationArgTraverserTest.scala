package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.InvocationArgClassifier
import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.matchers.ArgumentContextMatcher.eqArgumentContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class DefaultInvocationArgTraverserTest extends UnitTestSuite {

  private val expressionTraverser = mock[ExpressionTraverser]
  private val invocationArgClassifier = mock[InvocationArgClassifier]

  private val invocationArgTraverser = new DefaultInvocationArgTraverser(
    expressionTraverser,
    invocationArgClassifier
  )

  test("traverse when arg is a Lit and passed by value") {
    val arg = q"1"
    val context = ArgumentContext(index = 0)

    when(invocationArgClassifier.isPassedByName(eqArgumentContext(context))).thenReturn(false)

    invocationArgTraverser.traverse(arg, context)

    verify(expressionTraverser).traverse(eqTree(arg))
  }

  test("traverse when arg is a Lit and passed by name") {
    val arg = q"1"
    val context = ArgumentContext(index = 0)
    val expectedArg = q"() => 1"

    when(invocationArgClassifier.isPassedByName(eqArgumentContext(context))).thenReturn(true)

    invocationArgTraverser.traverse(arg, context)

    verify(expressionTraverser).traverse(eqTree(expectedArg))
  }

  test("traverse when arg is a Term.Apply and passed by value") {
    val arg = q"execute(x)"
    val context = ArgumentContext(index = 0)

    when(invocationArgClassifier.isPassedByName(eqArgumentContext(context))).thenReturn(false)

    invocationArgTraverser.traverse(arg, context)

    verify(expressionTraverser).traverse(eqTree(arg))
  }

  test("traverse when arg is a Term.Apply and passed by name") {
    val arg = q"execute(x)"
    val context = ArgumentContext(index = 0)
    val expectedArg = q"() => execute(x)"

    when(invocationArgClassifier.isPassedByName(eqArgumentContext(context))).thenReturn(true)

    invocationArgTraverser.traverse(arg, context)

    verify(expressionTraverser).traverse(eqTree(expectedArg))
  }
}
