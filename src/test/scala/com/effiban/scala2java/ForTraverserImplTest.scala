package com.effiban.scala2java

import com.effiban.scala2java.stubs.StubForVariantTraverser

import scala.meta.Enumerator.Generator
import scala.meta.Term.For
import scala.meta.{Pat, Term}

class ForTraverserImplTest extends UnitTestSuite {

  private val forTraverser = new ForTraverserImpl(new StubForVariantTraverser())

  test("traverse") {
    val `for` = For(
      enums = List(
        Generator(pat = Pat.Var(Term.Name("x")), rhs = Term.Name("xs")),
        Generator(pat = Pat.Var(Term.Name("y")), rhs = Term.Name("ys"))
      ),
      body = Term.Name("result")
    )
    forTraverser.traverse(`for`)

    outputWriter.toString shouldBe
      """|/**
         |* STUB 'FOR':
         |* Enumerators: List(x <- xs, y <- ys)
         |* Body: result
         |* Final Function Name: "forEach"
         |*/
         |""".stripMargin
  }

}
