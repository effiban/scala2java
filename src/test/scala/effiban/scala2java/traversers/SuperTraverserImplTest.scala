package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.Term.Super
import scala.meta.{Name, Term}

class SuperTraverserImplTest extends UnitTestSuite {

  private val nameTraverser = mock[NameTraverser]

  private val superTraverser = new SuperTraverserImpl(nameTraverser)


  test("traverse() without clauses") {
    val `super` = Super(thisp = Name.Anonymous(), superp = Name.Anonymous())
    superTraverser.traverse(`super`)

    outputWriter.toString shouldBe "super"
  }

  test("traverse() with 'thisp' clause only") {
    val name = Term.Name("EnclosingClass")

    doWrite("EnclosingClass").when(nameTraverser).traverse(eqTree(name))

    superTraverser.traverse(Super(thisp = name, superp = Name.Anonymous()))

    outputWriter.toString shouldBe "EnclosingClass.super"
  }

  test("traverse() with both clauses") {
    val thisName = Term.Name("EnclosingClass")
    val superName = Term.Name("SuperTrait")

    doWrite("EnclosingClass").when(nameTraverser).traverse(eqTree(thisName))
    doWrite("/* extends SuperTrait */").when(nameTraverser).traverse(eqTree(superName))

    superTraverser.traverse(Super(thisp = thisName, superp = superName))

    outputWriter.toString shouldBe "EnclosingClass.super/* extends SuperTrait */"
  }

  test("traverse() with 'superp' clause only") {
    val superName = Term.Name("SuperTrait")

    doWrite("/* extends SuperTrait */").when(nameTraverser).traverse(eqTree(superName))

    superTraverser.traverse(Super(thisp = Name.Anonymous(), superp = superName))

    outputWriter.toString shouldBe "super/* extends SuperTrait */"
  }
}
