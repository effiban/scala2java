package com.effiban.scala2java

import org.mockito.ArgumentMatchers.any

import scala.meta.Term.{Block, If, Return, While}
import scala.meta.{Init, Lit, Stat, Term}

class BlockTraverserImplTest extends UnitTestSuite {

  private val initTraverser = mock[InitTraverser]
  private val ifTraverser = mock[IfTraverser]
  private val whileTraverser = mock[WhileTraverser]
  private val returnTraverser: ReturnTraverser = mock[ReturnTraverser]
  private val statTraverser = mock[StatTraverser]

  private val blockTraverser = new BlockTraverserImpl(
    initTraverser,
    ifTraverser,
    whileTraverser,
    returnTraverser,
    statTraverser)

  override def beforeEach(): Unit = {
    super.beforeEach()
    doAnswer((init: Init) => outputWriter.write(init.toString())).when(initTraverser).traverse(any[Init])
    doAnswer((`if`: If, shouldReturnValue: Boolean) => outputWriter.write(s"${`if`}//shouldReturnValue=$shouldReturnValue\n"))
      .when(ifTraverser).traverse(any[If], any[Boolean])
    doAnswer((`while`: While) => outputWriter.write(s"${`while`}\n")).when(whileTraverser).traverse(any[While])
    doAnswer((`return`: Return) => outputWriter.write(`return`.toString)).when(returnTraverser).traverse(any[Return])
    doAnswer((stat: Stat) => outputWriter.write(stat.toString())).when(statTraverser).traverse(any[Stat])
  }

  test("traverse() when block is empty") {
    blockTraverser.traverse(Block(List.empty))

    outputWriter.toString shouldBe
      """ {
        |}
        |""".stripMargin
  }

  test("traverse() when block has one regular statement and should not return") {
    blockTraverser.traverse(Block(List(Term.Name("some_statement"))))

    outputWriter.toString shouldBe
      """ {
        |some_statement;
        |}
        |""".stripMargin
  }

  test("traverse() when block has two regular statements and should not return") {
    blockTraverser.traverse(
      Block(
        List(
          Term.Name("some_statement"),
          Term.Name("last_statement"),
        ))
    )

    outputWriter.toString shouldBe
      """ {
        |some_statement;
        |last_statement;
        |}
        |""".stripMargin
  }

  test("traverse() when block has one regular statement and should return") {
    blockTraverser.traverse(
      block = Block(List(Term.Name("some_statement"))),
      shouldReturnValue = true
    )

    outputWriter.toString shouldBe
      """ {
        |return some_statement;
        |}
        |""".stripMargin
  }

  test("traverse() when block has two regular statements and should return") {
    blockTraverser.traverse(
      Block(
        List(
          Term.Name("some_statement"),
          Term.Name("last_statement"),
        )),
      shouldReturnValue = true
    )

    outputWriter.toString shouldBe
      """ {
        |some_statement;
        |return last_statement;
        |}
        |""".stripMargin
  }

  test("traverse() when block has an 'if' statement that is not the last") {
    blockTraverser.traverse(
      Block(
        List(
          Term.If(cond = Term.ApplyInfix(lhs = Term.Name("x"), op = Term.Name("<"), targs = List.empty, args = List(Lit.Int(3))),
            thenp = Term.Apply(fun = Term.Name("doSomething"), args = List.empty),
            elsep = Lit.Unit()
          ),
          Term.Name("last_statement")
        )
      )
    )

    outputWriter.toString shouldBe
      """ {
        |if (x < 3) doSomething()//shouldReturnValue=false
        |last_statement;
        |}
        |""".stripMargin
  }

  test("traverse() when block has an 'if' statement that is the last and should not return") {
    blockTraverser.traverse(
      Block(
        List(
          Term.Name("first_statement"),
          Term.If(cond = Term.ApplyInfix(lhs = Term.Name("x"), op = Term.Name("<"), targs = List.empty, args = List(Lit.Int(3))),
            thenp = Term.Apply(fun = Term.Name("doSomething"), args = List.empty),
            elsep = Lit.Unit()
          )
        )
      )
    )

    outputWriter.toString shouldBe
      """ {
        |first_statement;
        |if (x < 3) doSomething()//shouldReturnValue=false
        |}
        |""".stripMargin
  }

  test("traverse() when block has an 'if' statement that is the last and should return") {
    blockTraverser.traverse(
      Block(
        List(
          Term.Name("first_statement"),
          Term.If(cond = Term.ApplyInfix(lhs = Term.Name("x"), op = Term.Name("<"), targs = List.empty, args = List(Lit.Int(3))),
            thenp = Term.Apply(fun = Term.Name("doSomething"), args = List.empty),
            elsep = Lit.Unit()
          )
        )
      ),
      shouldReturnValue = true
    )

    outputWriter.toString shouldBe
      """ {
        |first_statement;
        |if (x < 3) doSomething()//shouldReturnValue=true
        |}
        |""".stripMargin
  }

  test("traverse() when block has a 'while' statement that is not the last") {
    blockTraverser.traverse(
      Block(
        List(
          Term.While(expr = Term.ApplyInfix(lhs = Term.Name("x"), op = Term.Name("<"), targs = List.empty, args = List(Lit.Int(3))),
            body = Term.Apply(fun = Term.Name("doSomething"), args = List.empty)
          ),
          Term.Name("last_statement")
        )
      )
    )

    outputWriter.toString shouldBe
      """ {
        |while (x < 3) doSomething()
        |last_statement;
        |}
        |""".stripMargin
  }

  test("traverse() when block has a 'while' statement that is the last") {
    blockTraverser.traverse(
      Block(
        List(
          Term.Name("first_statement"),
          Term.While(expr = Term.ApplyInfix(lhs = Term.Name("x"), op = Term.Name("<"), targs = List.empty, args = List(Lit.Int(3))),
            body = Term.Apply(fun = Term.Name("doSomething"), args = List.empty)
          )
        )
      )
    )

    outputWriter.toString shouldBe
      """ {
        |first_statement;
        |while (x < 3) doSomething()
        |}
        |""".stripMargin
  }
}
