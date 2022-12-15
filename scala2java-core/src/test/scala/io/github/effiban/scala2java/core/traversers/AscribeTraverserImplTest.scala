package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

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
