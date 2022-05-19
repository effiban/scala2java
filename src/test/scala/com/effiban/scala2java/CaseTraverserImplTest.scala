package com.effiban.scala2java

import org.mockito.ArgumentMatchers.any

import scala.meta.{Case, Lit, Pat, Term}

class CaseTraverserImplTest extends UnitTestSuite {

  private val patTraverser = mock[PatTraverser]
  private val termTraverser = mock[TermTraverser]

  private val caseTraverser = new CaseTraverserImpl(
    patTraverser,
    termTraverser)

  override def beforeEach(): Unit = {
    super.beforeEach()
    doAnswer((pat: Pat) => outputWriter.write(pat.toString())).when(patTraverser).traverse(any[Pat])
    doAnswer((term: Term) => outputWriter.write(term.toString())).when(termTraverser).traverse(any[Term])
  }

  test("traverse() without condition") {
    caseTraverser.traverse(
      Case(pat = Lit.String("someValue"),
        cond = None,
        body = Lit.Int(3)
      )
    )

    outputWriter.toString shouldBe
      """case "someValue" -> 3;
        |""".stripMargin
  }

  test("traverse() with condition") {
    caseTraverser.traverse(
      Case(pat = Lit.String("someValue"),
        cond = Some(Lit.String("someOtherValue")),
        body = Lit.Int(3)
      )
    )

    outputWriter.toString shouldBe
      """case "someValue" && ("someOtherValue") -> 3;
        |""".stripMargin
  }
}
