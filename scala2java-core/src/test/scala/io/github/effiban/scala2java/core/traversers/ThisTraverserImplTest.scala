package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.ThisRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Name
import scala.meta.Term.This

class ThisTraverserImplTest extends UnitTestSuite {

  private val nameTraverser = mock[NameTraverser]
  private val thisRenderer = mock[ThisRenderer]

  private val thisTraverser = new ThisTraverserImpl(nameTraverser, thisRenderer)

  test("traverse() when name is anonymous") {
    thisTraverser.traverse(This(Name.Anonymous()))

    verify(thisRenderer).render(eqTree(This(Name.Anonymous())))
  }

  test("traverse() when name is specified") {
    val name = Name.Indeterminate("EnclosingClass")
    val traversedName = Name.Indeterminate("TraversedEnclosingClass")

    doReturn(traversedName).when(nameTraverser).traverse(eqTree(name))

    thisTraverser.traverse(This(name))

    verify(thisRenderer).render(eqTree(This(traversedName)))
  }
}
