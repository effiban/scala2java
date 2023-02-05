package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.Throw
import scala.meta.{Init, Name, Term, Type}

class ThrowTraverserImplTest extends UnitTestSuite {

  private val expressionTraverser = mock[ExpressionTraverser]

  private val throwTraverser = new ThrowTraverserImpl(expressionTraverser)

  test("traverse") {
    val exception = Term.New(Init(tpe = Type.Name("IllegalStateException"), name = Name.Anonymous(), argss = Nil))
    val `throw` = Throw(exception)

    throwTraverser.traverse(`throw`)

    verify(expressionTraverser).traverse(eqTree(exception))
  }
}
