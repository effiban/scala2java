package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.SuperRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Name
import scala.meta.Term.Super

class SuperTraverserImplTest extends UnitTestSuite {

  private val nameTraverser = mock[NameTraverser]
  private val superRenderer = mock[SuperRenderer]

  private val superTraverser = new SuperTraverserImpl(nameTraverser, superRenderer)


  test("traverse() without clauses") {
    val `super` = Super(thisp = Name.Anonymous(), superp = Name.Anonymous())

    superTraverser.traverse(`super`)

    verify(superRenderer).render(`super`)
  }

  test("traverse() with 'thisp' clause only") {
    val name = Name.Indeterminate("EnclosingClass")
    val traversedName = Name.Indeterminate("TraversedEnclosingClass")
    val `super` = Super(thisp = name, superp = Name.Anonymous())
    val traversedSuper = Super(thisp = traversedName, superp = Name.Anonymous())

    doReturn(traversedName).when(nameTraverser).traverse(eqTree(name))

    superTraverser.traverse(`super`)

    verify(superRenderer).render(eqTree(traversedSuper))
  }

  test("traverse() with both clauses") {
    val thisName = Name.Indeterminate("EnclosingClass")
    val traversedThisName = Name.Indeterminate("TraversedEnclosingClass")
    val superName = Name.Indeterminate("SuperTrait")
    val `super` = Super(thisp = thisName, superp = superName)
    val traversedSuper = Super(thisp = traversedThisName, superp = superName)

    doReturn(traversedThisName).when(nameTraverser).traverse(eqTree(thisName))

    superTraverser.traverse(`super`)

    verify(superRenderer).render(eqTree(traversedSuper))
  }

  test("traverse() with 'superp' clause only") {
    val superName = Name.Indeterminate("SuperTrait")
    val `super` = Super(thisp = Name.Anonymous(), superp = superName)

    superTraverser.traverse(`super`)

    verify(superRenderer).render(eqTree(`super`))
  }
}
