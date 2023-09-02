package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.{Super, This}
import scala.meta.{Name, XtensionQuasiquoteTerm}

class DefaultTermRefTraverserImplTest extends UnitTestSuite {

  private val defaultTermSelectTraverser = mock[DefaultTermSelectTraverser]

  private val defaultTermRefTraverser = new DefaultTermRefTraverserImpl(defaultTermSelectTraverser)

  test("traverse 'this'") {
    val `this` = This(Name.Indeterminate("MyName"))

    defaultTermRefTraverser.traverse(`this`).structure shouldBe `this`.structure
  }

  test("traverse 'super'") {
    val `super` = Super(thisp = Name.Indeterminate("superName"), superp = Name.Anonymous())

    defaultTermRefTraverser.traverse(`super`).structure shouldBe `super`.structure
  }

  test("traverse termName") {
    val termName = q"x"

    defaultTermRefTraverser.traverse(termName).structure shouldBe termName.structure
  }

  test("traverse termSelect") {
    val termSelect = q"X.x"
    val traversedTermSelect = q"Y.x"

    doReturn(traversedTermSelect).when(defaultTermSelectTraverser).traverse(eqTree(termSelect))

    defaultTermRefTraverser.traverse(termSelect).structure shouldBe traversedTermSelect.structure
  }
}
