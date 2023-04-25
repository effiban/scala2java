package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.matchers.TermSelectContextMatcher.eqTermSelectContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.{ApplyUnary, Super, This}
import scala.meta.{Name, Term}

class ExpressionTermRefTraverserTest extends UnitTestSuite {

  private val termNameTraverser = mock[TermNameTraverser]
  private val termSelectTraverser = mock[ExpressionTermSelectTraverser]
  private val applyUnaryTraverser = mock[ApplyUnaryTraverser]
  private val defaultTermRefTraverser = mock[DefaultTermRefTraverser]

  private val expressionTermRefTraverser = new ExpressionTermRefTraverser(
    termNameTraverser,
    termSelectTraverser,
    applyUnaryTraverser,
    defaultTermRefTraverser
  )
  
  test("traverse 'this'") {
    val `this` = This(Name.Indeterminate("MyName"))

    expressionTermRefTraverser.traverse(`this`)

    verify(defaultTermRefTraverser).traverse(eqTree(`this`))
  }

  test("traverse 'super'") {
    val `super` = Super(thisp = Name.Indeterminate("superName"), superp = Name.Anonymous())

    expressionTermRefTraverser.traverse(`super`)

    verify(defaultTermRefTraverser).traverse(eqTree(`super`))
  }

  test("traverse termName") {
    val termName = Term.Name("x")

    expressionTermRefTraverser.traverse(termName)

    verify(termNameTraverser).traverse(eqTree(termName))
  }

  test("traverse termSelect") {
    val termSelect = Term.Select(Term.Name("X"), Term.Name("x"))

    expressionTermRefTraverser.traverse(termSelect)

    verify(termSelectTraverser).traverse(eqTree(termSelect), eqTermSelectContext(TermSelectContext()))
  }

  test("traverse applyUnary") {
    val applyUnary = ApplyUnary(Term.Name("!"), Term.Name("x"))

    expressionTermRefTraverser.traverse(applyUnary)

    verify(applyUnaryTraverser).traverse(eqTree(applyUnary))
  }
}
