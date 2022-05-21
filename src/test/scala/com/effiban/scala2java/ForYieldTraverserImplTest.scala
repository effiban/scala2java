package com.effiban.scala2java

import com.effiban.scala2java.stubs.StubForVariantTraverser

import scala.meta.Enumerator.Generator
import scala.meta.Term.ForYield
import scala.meta.{Pat, Term}

class ForYieldTraverserImplTest extends UnitTestSuite {

  private val forYieldTraverser = new ForYieldTraverserImpl(new StubForVariantTraverser())

  test("traverse") {
    val forYield = ForYield(
      enums = List(
        Generator(pat = Pat.Var(Term.Name("x")), rhs = Term.Name("xs")),
        Generator(pat = Pat.Var(Term.Name("y")), rhs = Term.Name("ys"))
      ),
      body = Term.Name("result")
    )
    forYieldTraverser.traverse(forYield)

    outputWriter.toString shouldBe
      """|/**
         |* STUB 'FOR':
         |* Enumerators: List(x <- xs, y <- ys)
         |* Body: result
         |* Final Function Name: "map"
         |*/
         |""".stripMargin
  }

}
