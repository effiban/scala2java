package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.NameRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Name, Term, XtensionQuasiquoteType}

class NameTraverserImplTest extends UnitTestSuite {

  private val typeNameTraverser = mock[TypeNameTraverser]
  private val nameRenderer = mock[NameRenderer]


  private val nameTraverser = new NameTraverserImpl(typeNameTraverser, nameRenderer)

  test("traverse for Name.Anonymous") {
    nameTraverser.traverse(Name.Anonymous())

    verify(nameRenderer).render(eqTree(Name.Anonymous()))
  }

  test("traverse for Name.Indeterminate") {
    val name = Name.Indeterminate("myName")

    nameTraverser.traverse(name)

    verify(nameRenderer).render(eqTree(name))
  }

  test("traverse for Term.Name") {
    val name = Term.Name("myTermName")

    nameTraverser.traverse(name)

    verify(nameRenderer).render(eqTree(name))
  }

  test("traverse for Type.Name") {
    val name = t"myTypeName"
    val traversedName = t"myTraversedTypeName"

    doReturn(traversedName).when(typeNameTraverser).traverse(name)

    nameTraverser.traverse(name)

    verify(nameRenderer).render(eqTree(traversedName))
  }
}
