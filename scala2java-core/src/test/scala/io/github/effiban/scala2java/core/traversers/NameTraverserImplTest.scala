package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.{NameIndeterminateRenderer, TermNameRenderer}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Name, Term, Type}

class NameTraverserImplTest extends UnitTestSuite {

  private val nameIndeterminateRenderer = mock[NameIndeterminateRenderer]
  private val termNameRenderer = mock[TermNameRenderer]
  private val typeNameTraverser = mock[TypeNameTraverser]


  private val nameTraverser = new NameTraverserImpl(
    nameIndeterminateRenderer,
    termNameRenderer,
    typeNameTraverser)

  test("traverse for Name.Anonymous") {
    nameTraverser.traverse(Name.Anonymous())
  }

  test("traverse for Name.Indeterminate") {
    val name = Name.Indeterminate("myName")

    nameTraverser.traverse(name)

    verify(nameIndeterminateRenderer).render(eqTree(name))
  }

  test("traverse for Term.Name") {
    val name = Term.Name("myTermName")

    nameTraverser.traverse(name)

    verify(termNameRenderer).render(eqTree(name))
  }

  test("traverse for Term.Type") {
    val name = Type.Name("myTypeName")

    nameTraverser.traverse(name)

    verify(typeNameTraverser).traverse(eqTree(name))
  }
}
