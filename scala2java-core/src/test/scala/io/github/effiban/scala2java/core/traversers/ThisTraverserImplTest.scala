package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Term.This
import scala.meta.{Name, Type}

class ThisTraverserImplTest extends UnitTestSuite {

  private val nameTraverser = mock[NameTraverser]

  private val thisTraverser = new ThisTraverserImpl(nameTraverser)

  test("traverse() when name is anonymous") {
    thisTraverser.traverse(This(Name.Anonymous()))

    outputWriter.toString shouldBe "this"
  }

  test("traverse() when name is specified") {
    val name = Type.Name("EnclosingClass")

    doWrite("EnclosingClass").when(nameTraverser).traverse(eqTree(name))

    thisTraverser.traverse(This(name))

    outputWriter.toString shouldBe "EnclosingClass.this"
  }
}
