package com.effiban.scala2java

import org.mockito.ArgumentMatchers.any

import scala.meta.Term

class ApplyUnaryTraverserImplTest extends UnitTestSuite {

  private val termNameTraverser = mock[TermNameTraverser]
  private val termTraverser = mock[TermTraverser]

  private val applyUnaryTraverser = new ApplyUnaryTraverserImpl(termNameTraverser, termTraverser)

  override def beforeEach(): Unit = {
    super.beforeEach()
    doAnswer((termName: Term.Name) => outputWriter.write(termName.toString())).when(termNameTraverser).traverse(any[Term.Name])
    doAnswer((term: Term) => outputWriter.write(term.toString())).when(termTraverser).traverse(any[Term])
  }

  test("traverse") {
    applyUnaryTraverser.traverse(Term.ApplyUnary(op = Term.Name("!"), arg = Term.Name("myFlag")))

    outputWriter.toString shouldBe "!myFlag"
  }
}
