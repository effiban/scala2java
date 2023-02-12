package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.InvocationArgClassifier
import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.entities.ArgumentCoordinates
import io.github.effiban.scala2java.core.matchers.ArgumentCoordinatesMatcher.eqArgumentCoordinates
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

  test("traverse when has a parent, has a name, and is a Lit passed by value") {
    val parent = q"foo(x)"
    val name = q"arg1"
    val arg = q"1"
    val context = ArgumentContext(maybeParent = Some(parent), maybeName = Some(name), index = 0)
    val coords = ArgumentCoordinates(parent = parent, maybeName = Some(name), index = 0)

    when(invocationArgClassifier.isPassedByName(eqArgumentCoordinates(coords))).thenReturn(false)

    invocationArgTraverser.traverse(arg, context)

    verify(expressionTraverser).traverse(eqTree(arg))
  }

  test("traverse when has a parent, has no name, and is a Lit passed by value") {
    val parent = q"foo(x)"
    val arg = q"1"
    val context = ArgumentContext(maybeParent = Some(parent), index = 0)
    val coords = ArgumentCoordinates(parent = parent, index = 0)

    when(invocationArgClassifier.isPassedByName(eqArgumentCoordinates(coords))).thenReturn(false)

    invocationArgTraverser.traverse(arg, context)

    verify(expressionTraverser).traverse(eqTree(arg))
  }

  test("traverse when has a parent, and is a Lit passed by name") {
    val parent = q"foo(x)"
    val arg = q"1"
    val expectedArg = q"() => 1"
    val context = ArgumentContext(maybeParent = Some(parent),index = 0)
    val coords = ArgumentCoordinates(parent = parent, index = 0)

    when(invocationArgClassifier.isPassedByName(eqArgumentCoordinates(coords))).thenReturn(true)

    invocationArgTraverser.traverse(arg, context)

    verify(expressionTraverser).traverse(eqTree(expectedArg))
  }

  test("traverse when has a parent, and is a Term.Apply passed by value") {
    val parent = q"doCallback(x)"
    val arg = q"foo(y)"
    val context = ArgumentContext(maybeParent = Some(parent), index = 0)
    val coords = ArgumentCoordinates(parent = parent, index = 0)

    when(invocationArgClassifier.isPassedByName(eqArgumentCoordinates(coords))).thenReturn(false)

    invocationArgTraverser.traverse(arg, context)

    verify(expressionTraverser).traverse(eqTree(arg))
  }

  test("traverse when has a parent, and is a Term.Apply passed by name") {
    val parent = q"doCallback(x)"
    val arg = q"foo(y)"
    val expectedArg = q"() => foo(y)"
    val context = ArgumentContext(maybeParent = Some(parent), index = 0)
    val coords = ArgumentCoordinates(parent = parent, index = 0)

    when(invocationArgClassifier.isPassedByName(eqArgumentCoordinates(coords))).thenReturn(true)

    invocationArgTraverser.traverse(arg, context)

    verify(expressionTraverser).traverse(eqTree(expectedArg))
  }

  test("traverse when has no parent") {
    val arg = q"xyz"
    val context = ArgumentContext(index = 0)

    invocationArgTraverser.traverse(arg, context)

    verify(expressionTraverser).traverse(eqTree(arg))

    verifyNoMoreInteractions(invocationArgClassifier)
  }
}
