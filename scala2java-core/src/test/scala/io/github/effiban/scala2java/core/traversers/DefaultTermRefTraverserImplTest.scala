package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.matchers.TermSelectContextMatcher.eqTermSelectContext
import io.github.effiban.scala2java.core.renderers.TermNameRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.{Super, This}
import scala.meta.{Name, Term}

class DefaultTermRefTraverserImplTest extends UnitTestSuite {

  private val thisTraverser = mock[ThisTraverser]
  private val superTraverser = mock[SuperTraverser]
  private val termNameRenderer = mock[TermNameRenderer]
  private val termSelectTraverser = mock[TermSelectTraverser]

  private val defaultTermRefTraverser = new DefaultTermRefTraverserImpl(
    thisTraverser,
    superTraverser,
    termNameRenderer,
    termSelectTraverser
  )
  
  test("traverse 'this'") {
    val `this` = This(Name.Indeterminate("MyName"))

    defaultTermRefTraverser.traverse(`this`)

    verify(thisTraverser).traverse(eqTree(`this`))
  }

  test("traverse 'super'") {
    val `super` = Super(thisp = Name.Indeterminate("superName"), superp = Name.Anonymous())

    defaultTermRefTraverser.traverse(`super`)

    verify(superTraverser).traverse(eqTree(`super`))
  }

  test("traverse termName") {
    val termName = Term.Name("x")

    defaultTermRefTraverser.traverse(termName)

    verify(termNameRenderer).render(eqTree(termName))
  }

  test("traverse termSelect") {
    val termSelect = Term.Select(Term.Name("X"), Term.Name("x"))

    defaultTermRefTraverser.traverse(termSelect)

    verify(termSelectTraverser).traverse(eqTree(termSelect), eqTermSelectContext(TermSelectContext()))
  }
}
