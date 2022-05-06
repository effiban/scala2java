package com.effiban.scala2java

import org.mockito.ArgumentMatchers.any

import scala.meta.{Lit, Term, Type}

class AscribeTraverserImplTest extends UnitTestSuite {

  private val typeTraverser = mock[TypeTraverser]
  private val termTraverser = mock[TermTraverser]

  private val ascribeTraverser = new AscribeTraverserImpl(typeTraverser, termTraverser)

  override def beforeEach(): Unit = {
    super.beforeEach()
    doAnswer((tpe: Type) => outputWriter.write(tpe.toString())).when(typeTraverser).traverse(any[Type])
    doAnswer((term: Term) => outputWriter.write(term.toString())).when(termTraverser).traverse(any[Term])
  }

  test("traverse") {
    ascribeTraverser.traverse(Term.Ascribe(expr = Lit.Int(22), tpe = Type.Name("MyType")))

    outputWriter.toString shouldBe "(MyType)22"
  }
}
