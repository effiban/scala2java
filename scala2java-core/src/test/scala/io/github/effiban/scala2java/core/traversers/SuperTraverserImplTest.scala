package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.NameRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Name
import scala.meta.Term.Super

class SuperTraverserImplTest extends UnitTestSuite {

  private val nameTraverser = mock[NameTraverser]
  private val nameRenderer = mock[NameRenderer]

  private val superTraverser = new SuperTraverserImpl(nameTraverser, nameRenderer)


  test("traverse() without clauses") {
    val `super` = Super(thisp = Name.Anonymous(), superp = Name.Anonymous())

    superTraverser.traverse(`super`)

    outputWriter.toString shouldBe "super"
  }

  test("traverse() with 'thisp' clause only") {
    val name = Name.Indeterminate("EnclosingClass")
    val traversedName = Name.Indeterminate("TraversedEnclosingClass")

    doReturn(traversedName).when(nameTraverser).traverse(eqTree(name))
    doWrite("TraversedEnclosingClass").when(nameRenderer).render(eqTree(traversedName))

    superTraverser.traverse(Super(thisp = name, superp = Name.Anonymous()))

    outputWriter.toString shouldBe "TraversedEnclosingClass.super"
  }

  test("traverse() with both clauses") {
    val thisName = Name.Indeterminate("EnclosingClass")
    val traversedThisName = Name.Indeterminate("TraversedEnclosingClass")
    val superName = Name.Indeterminate("SuperTrait")

    doReturn(traversedThisName).when(nameTraverser).traverse(eqTree(thisName))
    doWrite("TraversedEnclosingClass").when(nameRenderer).render(eqTree(traversedThisName))
    doWrite("/* extends SuperTrait */").when(nameTraverser).traverse(eqTree(superName))

    superTraverser.traverse(Super(thisp = thisName, superp = superName))

    outputWriter.toString shouldBe "TraversedEnclosingClass.super/* extends SuperTrait */"
  }

  test("traverse() with 'superp' clause only") {
    val superName = Name.Indeterminate("SuperTrait")

    superTraverser.traverse(Super(thisp = Name.Anonymous(), superp = superName))

    outputWriter.toString shouldBe "super/* extends SuperTrait */"
  }
}
