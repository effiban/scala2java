package com.effiban.scala2java

import com.effiban.scala2java.stubs._

import scala.meta.Term.{Block, This}
import scala.meta.{Init, Lit, Name, Term, Type}

class BlockTraverserImplTest extends UnitTestSuite {

  private val blockTraverser = new BlockTraverserImpl(
    new StubInitTraverser(),
    new StubIfTraverser(),
    new StubWhileTraverser(),
    new StubReturnTraverser(),
    new StubStatTraverser())


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
        |if (x < 3) {
        |doSomething()/* shouldReturnValue=false */
        |}
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
        |if (x < 3) {
        |doSomething()/* shouldReturnValue=false */
        |}
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
        |if (x < 3) {
        |doSomething()/* shouldReturnValue=true */
        |}
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
        |while (x < 3) {
        |doSomething()
        |}
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
        |while (x < 3) {
        |doSomething()
        |}
        |}
        |""".stripMargin
  }

  test("traverse() when an 'init' is passed") {
    blockTraverser.traverse(
      Block(
        List(
          Term.Name("first_statement"),
          Term.Name("second_statement")
        )
      ),
      maybeInit = Some(
        Init(
          tpe = Type.Singleton(ref = This(Name.Anonymous())),
          name = Name.Anonymous(),
          argss = List(List(Lit.String("dummy"))))
      )
    )

    outputWriter.toString shouldBe
      """ {
        |this("dummy");
        |first_statement;
        |second_statement;
        |}
        |""".stripMargin
  }
}
