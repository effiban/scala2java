package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubPatTraverser, StubTermTraverser}

import scala.meta.{Case, Lit}

class CaseTraverserImplTest extends UnitTestSuite {

  private val caseTraverser = new CaseTraverserImpl(
    new StubPatTraverser(),
    new StubTermTraverser())


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
