package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite

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
