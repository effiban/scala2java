package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.testsuites.UnitTestSuite

import scala.meta.{Lit, Term, Type}

class AscribeTraverserImplTest extends UnitTestSuite {
  private val typeTraverser = mock[TypeTraverser]
  private val termTraverser = mock[TermTraverser]

  private val ascribeTraverser = new AscribeTraverserImpl(typeTraverser, termTraverser)

  test("traverse") {
    val expr = Lit.Int(22)
    val typeName = Type.Name("MyType")

    doWrite("MyType").when(typeTraverser).traverse(eqTree(typeName))
    doWrite("22").when(termTraverser).traverse(eqTree(expr))

    ascribeTraverser.traverse(Term.Ascribe(expr = expr, tpe = typeName))

    outputWriter.toString shouldBe "(MyType)22"
  }
}