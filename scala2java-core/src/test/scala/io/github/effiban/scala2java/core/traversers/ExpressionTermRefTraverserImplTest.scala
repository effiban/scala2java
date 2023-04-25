package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.matchers.TermSelectContextMatcher.eqTermSelectContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.{ApplyUnary, Super, This}
import scala.meta.{Name, Term}

class ExpressionTermRefTraverserImplTest extends UnitTestSuite {

  private val thisTraverser = mock[ThisTraverser]
  private val superTraverser = mock[SuperTraverser]
  private val termNameTraverser = mock[TermNameTraverser]
  private val termSelectTraverser = mock[ExpressionTermSelectTraverser]
  private val applyUnaryTraverser = mock[ApplyUnaryTraverser]

  private val termRefTraverser = new ExpressionTermRefTraverser(
    thisTraverser,
    superTraverser,
    termNameTraverser,
    termSelectTraverser,
    applyUnaryTraverser
  )
  
  test("traverse 'this'") {
    val `this` = This(Name.Indeterminate("MyName"))

    termRefTraverser.traverse(`this`)

    verify(thisTraverser).traverse(eqTree(`this`))
  }

  test("traverse 'super'") {
    val `super` = Super(thisp = Name.Indeterminate("superName"), superp = Name.Anonymous())

    termRefTraverser.traverse(`super`)

    verify(superTraverser).traverse(eqTree(`super`))
  }

  test("traverse termName") {
    val termName = Term.Name("x")

    termRefTraverser.traverse(termName)

    verify(termNameTraverser).traverse(eqTree(termName))
  }

  test("traverse termSelect") {
    val termSelect = Term.Select(Term.Name("X"), Term.Name("x"))

    termRefTraverser.traverse(termSelect)

    verify(termSelectTraverser).traverse(eqTree(termSelect), eqTermSelectContext(TermSelectContext()))
  }

  test("traverse applyUnary") {
    val applyUnary = ApplyUnary(Term.Name("!"), Term.Name("x"))

    termRefTraverser.traverse(applyUnary)

    verify(applyUnaryTraverser).traverse(eqTree(applyUnary))
  }
}