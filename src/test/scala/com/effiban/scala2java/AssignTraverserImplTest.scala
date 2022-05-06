package com.effiban.scala2java

import org.mockito.ArgumentMatchers.any

import scala.meta.{Lit, Term}

class AssignTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]

  private val assignTraverser = new AssignTraverserImpl(termTraverser)

  override def beforeEach(): Unit = {
    super.beforeEach()
    doAnswer((term: Term) => outputWriter.write(term.toString())).when(termTraverser).traverse(any[Term])
  }

  test("traverse") {
    assignTraverser.traverse(Term.Assign(lhs = Term.Name("myVal"), rhs = Lit.Int(3)))

    outputWriter.toString shouldBe "myVal = 3"
  }
}
