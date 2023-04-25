package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.This
import scala.meta.{Name, XtensionStructure}

class ThisTraverserImplTest extends UnitTestSuite {

  private val nameTraverser = mock[NameTraverser]

  private val thisTraverser = new ThisTraverserImpl(nameTraverser)

  test("traverse() when name is anonymous") {
    thisTraverser.traverse(This(Name.Anonymous())).structure shouldBe This(Name.Anonymous()).structure
  }

  test("traverse() when name is specified") {
    val name = Name.Indeterminate("EnclosingClass")
    val traversedName = Name.Indeterminate("TraversedEnclosingClass")

    doReturn(traversedName).when(nameTraverser).traverse(eqTree(name))

    thisTraverser.traverse(This(name)).structure shouldBe This(traversedName).structure
  }
}
