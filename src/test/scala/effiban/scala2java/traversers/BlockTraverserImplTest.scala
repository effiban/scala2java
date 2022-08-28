package effiban.scala2java.traversers

import effiban.scala2java.entities.Decision.{No, Yes}
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.Term.{Block, Return, This}
import scala.meta.{Init, Lit, Name, Term, Type}

class BlockTraverserImplTest extends UnitTestSuite {

  private val SimpleStatement1Val = "statement1"
  private val SimpleStatement2Val = "statement2"

  private val IfStatementVal =
    """if (x < 3) {
       |    doSomething();
       |}""".stripMargin

  private val WhileStatementVal =
    """while (x < 3) {
      |    doSomething();
      |}""".stripMargin

  private val SimpleStatement1 = Term.Name(SimpleStatement1Val)
  private val SimpleStatement2 = Term.Name(SimpleStatement2Val)

  private val IfStatement = Term.If(
    cond = Term.ApplyInfix(lhs = Term.Name("x"), op = Term.Name("<"), targs = List.empty, args = List(Lit.Int(3))),
    thenp = Term.Apply(fun = Term.Name("doSomething"), args = List.empty),
    elsep = Lit.Unit()
  )

  private val WhileStatement = Term.While(
    expr = Term.ApplyInfix(lhs = Term.Name("x"), op = Term.Name("<"), targs = List.empty, args = List(Lit.Int(3))),
    body = Term.Apply(fun = Term.Name("doSomething"), args = List.empty)
  )

  private val initTraverser = mock[InitTraverser]
  private val ifTraverser = mock[IfTraverser]
  private val whileTraverser = mock[WhileTraverser]
  private val throwTraverser = mock[ThrowTraverser]
  private val returnTraverser = mock[ReturnTraverser]
  private val statTraverser = mock[StatTraverser]

  private val blockTraverser = new BlockTraverserImpl(
    initTraverser,
    ifTraverser,
    whileTraverser,
    throwTraverser,
    returnTraverser,
    statTraverser)


  test("traverse() when block is empty") {
    blockTraverser.traverse(Block(List.empty))

    outputWriter.toString shouldBe
      """ {
        |}
        |""".stripMargin
  }

  test("traverse() when block has one regular statement and should not return") {
    doWrite(SimpleStatement1Val).when(statTraverser).traverse(eqTree(SimpleStatement1))

    blockTraverser.traverse(Block(List(SimpleStatement1)))

    outputWriter.toString shouldBe
      s""" {
         |$SimpleStatement1Val;
         |}
         |""".stripMargin
  }

  test("traverse() when block has two regular statements and should not return") {
    doWrite(SimpleStatement1Val).when(statTraverser).traverse(eqTree(SimpleStatement1))
    doWrite(SimpleStatement2Val).when(statTraverser).traverse(eqTree(SimpleStatement2))

    blockTraverser.traverse(
      Block(
        List(
          SimpleStatement1,
          SimpleStatement2,
        ))
    )

    outputWriter.toString shouldBe
      s""" {
         |$SimpleStatement1Val;
         |$SimpleStatement2Val;
         |}
         |""".stripMargin
  }

  test("traverse() when block has one regular statement and should return") {
    doWrite(s"return $SimpleStatement1Val").when(returnTraverser).traverse(eqTree(Return(SimpleStatement1)))

    blockTraverser.traverse(
      stat = Block(List(SimpleStatement1)),
      shouldReturnValue = true
    )

    outputWriter.toString shouldBe
      s""" {
        |return $SimpleStatement1Val;
        |}
        |""".stripMargin
  }

  test("traverse() when block has two regular statements and should return") {
    doWrite(SimpleStatement1Val).when(statTraverser).traverse(eqTree(SimpleStatement1))
    doWrite(s"return $SimpleStatement2Val").when(returnTraverser).traverse(eqTree(Return(SimpleStatement2)))

    blockTraverser.traverse(
      Block(
        List(
          SimpleStatement1,
          SimpleStatement2,
        )),
      shouldReturnValue = true
    )

    outputWriter.toString shouldBe
      s""" {
        |$SimpleStatement1Val;
        |return $SimpleStatement2Val;
        |}
        |""".stripMargin
  }

  test("traverse() when block has an 'if' statement that is not the last") {
    doWrite(
      s"""$IfStatementVal
         |""".stripMargin
    ).when(ifTraverser).traverse(eqTree(IfStatement), shouldReturnValue = ArgumentMatchers.eq(No))
    doWrite(SimpleStatement2Val).when(statTraverser).traverse(eqTree(SimpleStatement2))

    blockTraverser.traverse(
      Block(
        List(
          IfStatement,
          SimpleStatement2
        )
      )
    )

    outputWriter.toString shouldBe
      s""" {
        |$IfStatementVal
        |$SimpleStatement2Val;
        |}
        |""".stripMargin
  }

  test("traverse() when block has an 'if' statement that is the last and should not return") {
    doWrite(SimpleStatement1Val).when(statTraverser).traverse(eqTree(SimpleStatement1))
    doWrite(
      s"""$IfStatementVal
         |""".stripMargin
    ).when(ifTraverser).traverse(eqTree(IfStatement), shouldReturnValue = ArgumentMatchers.eq(No))

    blockTraverser.traverse(
      Block(
        List(
          SimpleStatement1,
          IfStatement
        )
      )
    )

    outputWriter.toString shouldBe
      s""" {
        |$SimpleStatement1Val;
        |$IfStatementVal
        |}
        |""".stripMargin
  }

  test("traverse() when block has an 'if' statement that is the last and should return") {
    doWrite(SimpleStatement1Val).when(statTraverser).traverse(eqTree(SimpleStatement1))
    doWrite(
      s"""$IfStatementVal
         |""".stripMargin
    ).when(ifTraverser).traverse(eqTree(IfStatement), shouldReturnValue = ArgumentMatchers.eq(Yes))

    blockTraverser.traverse(
      Block(
        List(
          SimpleStatement1,
          IfStatement
        )
      ),
      shouldReturnValue = true
    )

    outputWriter.toString shouldBe
      s""" {
        |$SimpleStatement1Val;
        |$IfStatementVal
        |}
        |""".stripMargin
  }

  test("traverse() when block has a 'while' statement that is not the last") {
    doWrite(
      s"""$WhileStatementVal
         |""".stripMargin
    ).when(whileTraverser).traverse(eqTree(WhileStatement))
    doWrite(SimpleStatement2Val).when(statTraverser).traverse(eqTree(SimpleStatement2))

    blockTraverser.traverse(
      Block(
        List(
          WhileStatement,
          SimpleStatement2
        )
      )
    )

    outputWriter.toString shouldBe
      s""" {
        |$WhileStatementVal
        |$SimpleStatement2Val;
        |}
        |""".stripMargin
  }

  test("traverse() when block has a 'while' statement that is the last") {
    doWrite(SimpleStatement1Val).when(statTraverser).traverse(eqTree(SimpleStatement1))
    doWrite(
      s"""$WhileStatementVal
         |""".stripMargin
    ).when(whileTraverser).traverse(eqTree(WhileStatement))

    blockTraverser.traverse(
      Block(
        List(
          SimpleStatement1,
          WhileStatement
        )
      )
    )

    outputWriter.toString shouldBe
      s""" {
        |$SimpleStatement1Val;
        |$WhileStatementVal
        |}
        |""".stripMargin
  }

  test("traverse() when an 'init' is passed") {
    val init = Init(
      tpe = Type.Singleton(ref = This(Name.Anonymous())),
      name = Name.Anonymous(),
      argss = List(List(Lit.String("dummy")))
    )
    val initVal = "this(dummy)"

    doWrite(initVal).when(initTraverser).traverse(eqTree(init), ArgumentMatchers.eq(false))
    doWrite(SimpleStatement1Val).when(statTraverser).traverse(eqTree(SimpleStatement1))
    doWrite(SimpleStatement2Val).when(statTraverser).traverse(eqTree(SimpleStatement2))

    blockTraverser.traverse(
      Block(
        List(
          SimpleStatement1,
          SimpleStatement2
        )
      ),
      maybeInit = Some(init)
    )

    outputWriter.toString shouldBe
      s""" {
        |$initVal;
        |$SimpleStatement1Val;
        |$SimpleStatement2Val;
        |}
        |""".stripMargin
  }

  test("traverse() when block has a 'throw' statement and should return - expecting no 'return'") {
    val exceptionInit = Init(
      tpe = Type.Name("IllegalStateException"),
      name = Name.Anonymous(),
      argss = List(List(Lit.String("error")))
    )

    val throwStatement = Term.Throw(expr = Term.New(exceptionInit))
    val throwStatementVal = """throw new IllegalStateException("error")"""

    doWrite(SimpleStatement1Val).when(statTraverser).traverse(eqTree(SimpleStatement1))
    doWrite(throwStatementVal).when(throwTraverser).traverse(eqTree(throwStatement))

    blockTraverser.traverse(
      Block(
        List(
          SimpleStatement1,
          throwStatement
        )
      ),
      shouldReturnValue = true
    )

    outputWriter.toString shouldBe
      s""" {
         |$SimpleStatement1Val;
         |$throwStatementVal;
         |}
         |""".stripMargin
  }
}
