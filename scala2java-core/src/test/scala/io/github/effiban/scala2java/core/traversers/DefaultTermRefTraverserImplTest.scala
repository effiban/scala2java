package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.{Super, This}
import scala.meta.{Name, XtensionQuasiquoteTerm}

class DefaultTermRefTraverserImplTest extends UnitTestSuite {

  private val thisTraverser = mock[ThisTraverser]
  private val superTraverser = mock[SuperTraverser]
  private val defaultTermSelectTraverser = mock[DefaultTermSelectTraverser]

  private val defaultTermRefTraverser = new DefaultTermRefTraverserImpl(
    thisTraverser,
    superTraverser,
    defaultTermSelectTraverser
  )

  test("traverse 'this'") {
    val `this` = This(Name.Indeterminate("MyName"))
    val traversedThis = This(Name.Indeterminate("MyTraversedName"))

    doReturn(traversedThis).when(thisTraverser).traverse(eqTree(`this`))

    defaultTermRefTraverser.traverse(`this`).structure shouldBe traversedThis.structure
  }

  test("traverse 'super'") {
    val `super` = Super(thisp = Name.Indeterminate("superName"), superp = Name.Anonymous())
    val traversedSuper = Super(thisp = Name.Indeterminate("traversedSuperName"), superp = Name.Anonymous())

    doReturn(traversedSuper).when(superTraverser).traverse(eqTree(`super`))

    defaultTermRefTraverser.traverse(`super`).structure shouldBe traversedSuper.structure
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
