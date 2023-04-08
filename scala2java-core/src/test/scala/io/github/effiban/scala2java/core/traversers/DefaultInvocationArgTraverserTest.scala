package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.matchers.InvocationArgCoordinatesMatcher.eqArgumentCoordinates
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.InvocationArgCoordinates
import io.github.effiban.scala2java.spi.predicates.InvocationArgByNamePredicate
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class DefaultInvocationArgTraverserTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val invocationArgByNamePredicate = mock[InvocationArgByNamePredicate]

  private val invocationArgTraverser = new DefaultInvocationArgTraverser(
    expressionTermTraverser,
    invocationArgByNamePredicate
  )

  test("traverse when has an invocation, has a name, and is a Lit passed by value") {
    val invocation = q"foo(x)"
    val name = q"arg1"
    val arg = q"1"
    val context = ArgumentContext(maybeParent = Some(invocation), maybeName = Some(name), index = 0)
    val coords = InvocationArgCoordinates(invocation = invocation, maybeName = Some(name), index = 0)

    when(invocationArgByNamePredicate(eqArgumentCoordinates(coords))).thenReturn(false)

    invocationArgTraverser.traverse(arg, context)

    verify(expressionTermTraverser).traverse(eqTree(arg))
  }

  test("traverse when has an invocation, has no name, and is a Lit passed by value") {
    val invocation = q"foo(x)"
    val arg = q"1"
    val context = ArgumentContext(maybeParent = Some(invocation), index = 0)
    val coords = InvocationArgCoordinates(invocation = invocation, index = 0)

    when(invocationArgByNamePredicate(eqArgumentCoordinates(coords))).thenReturn(false)

    invocationArgTraverser.traverse(arg, context)

    verify(expressionTermTraverser).traverse(eqTree(arg))
  }

  test("traverse when has an invocation, and is a Lit passed by name") {
    val invocation = q"foo(x)"
    val arg = q"1"
    val expectedArg = q"() => 1"
    val context = ArgumentContext(maybeParent = Some(invocation),index = 0)
    val coords = InvocationArgCoordinates(invocation = invocation, index = 0)

    when(invocationArgByNamePredicate(eqArgumentCoordinates(coords))).thenReturn(true)

    invocationArgTraverser.traverse(arg, context)

    verify(expressionTermTraverser).traverse(eqTree(expectedArg))
  }

  test("traverse when has an invocation, and is a Term.Apply passed by value") {
    val invocation = q"doCallback(x)"
    val arg = q"foo(y)"
    val context = ArgumentContext(maybeParent = Some(invocation), index = 0)
    val coords = InvocationArgCoordinates(invocation = invocation, index = 0)

    when(invocationArgByNamePredicate(eqArgumentCoordinates(coords))).thenReturn(false)

    invocationArgTraverser.traverse(arg, context)

    verify(expressionTermTraverser).traverse(eqTree(arg))
  }

  test("traverse when has an invocation, and is a Term.Apply passed by name") {
    val invocation = q"doCallback(x)"
    val arg = q"foo(y)"
    val expectedArg = q"() => foo(y)"
    val context = ArgumentContext(maybeParent = Some(invocation), index = 0)
    val coords = InvocationArgCoordinates(invocation = invocation, index = 0)

    when(invocationArgByNamePredicate(eqArgumentCoordinates(coords))).thenReturn(true)

    invocationArgTraverser.traverse(arg, context)

    verify(expressionTermTraverser).traverse(eqTree(expectedArg))
  }

  test("traverse when has no invocation") {
    val arg = q"xyz"
    val context = ArgumentContext(index = 0)

    invocationArgTraverser.traverse(arg, context)

    verify(expressionTermTraverser).traverse(eqTree(arg))

    verifyNoMoreInteractions(invocationArgByNamePredicate)
  }
}
