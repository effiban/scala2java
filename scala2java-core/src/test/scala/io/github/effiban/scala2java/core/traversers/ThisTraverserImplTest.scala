package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.NameRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Name
import scala.meta.Term.This

class ThisTraverserImplTest extends UnitTestSuite {

  private val nameTraverser = mock[NameTraverser]
  private val nameRenderer = mock[NameRenderer]

  private val thisTraverser = new ThisTraverserImpl(nameTraverser, nameRenderer)

  test("traverse() when name is anonymous") {
    thisTraverser.traverse(This(Name.Anonymous()))

    outputWriter.toString shouldBe "this"
  }

  test("traverse() when name is specified") {
    val name = Name.Indeterminate("EnclosingClass")
    val traversedName = Name.Indeterminate("TraversedEnclosingClass")

    doReturn(traversedName).when(nameTraverser).traverse(eqTree(name))
    doWrite("TraversedEnclosingClass").when(nameRenderer).render(eqTree(traversedName))

    thisTraverser.traverse(This(name))

    outputWriter.toString shouldBe "TraversedEnclosingClass.this"
  }
}
