package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.{TermNameRenderer, ThisRenderer}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.{Super, This}
import scala.meta.{Name, Term}

class DefaultTermRefTraverserTest extends UnitTestSuite {

  private val thisTraverser = mock[ThisTraverser]
  private val thisRenderer = mock[ThisRenderer]
  private val superTraverser = mock[SuperTraverser]
  private val termNameRenderer = mock[TermNameRenderer]
  private val defaultTermSelectTraverser = mock[DefaultTermSelectTraverser]

  private val defaultTermRefTraverser = new DefaultTermRefTraverser(
    thisTraverser,
    thisRenderer,
    superTraverser,
    termNameRenderer,
    defaultTermSelectTraverser
  )

  test("traverse 'this'") {
    val `this` = This(Name.Indeterminate("MyName"))
    val traversedThis = This(Name.Indeterminate("MyTraversedName"))

    doReturn(traversedThis).when(thisTraverser).traverse(eqTree(`this`))

    defaultTermRefTraverser.traverse(`this`)

    verify(thisRenderer).render(eqTree(traversedThis))
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

    verify(defaultTermSelectTraverser).traverse(eqTree(termSelect))
  }
}
