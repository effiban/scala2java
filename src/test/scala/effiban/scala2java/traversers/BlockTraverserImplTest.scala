package effiban.scala2java.traversers

import effiban.scala2java.entities.Decision.{No, Yes}
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.Term.{Block, This}
import scala.meta.{Init, Lit, Name, Term, Type}

class BlockTraverserImplTest extends UnitTestSuite {

  private val SimpleStatement1Val = "statement1"
  private val SimpleStatement2Val = "statement2"

  private val SimpleStatement1 = Term.Name(SimpleStatement1Val)
  private val SimpleStatement2 = Term.Name(SimpleStatement2Val)

  private val initTraverser = mock[InitTraverser]
  private val blockStatTraverser = mock[BlockStatTraverser]

  private val blockTraverser = new BlockTraverserImpl(initTraverser, blockStatTraverser)


  test("traverse() when block is empty") {
    blockTraverser.traverse(Block(List.empty))

    outputWriter.toString shouldBe
      """ {
        |}
        |""".stripMargin
  }

  test("traverse() for one statement, shouldReturnValue=Yes") {
    doWrite(
      s"""return $SimpleStatement1Val;
         |""".stripMargin)
      .when(blockStatTraverser).traverseLast(eqTree(SimpleStatement1), ArgumentMatchers.eq(Yes))

    blockTraverser.traverse(SimpleStatement1, shouldReturnValue = Yes)

    outputWriter.toString shouldBe
      s""" {
         |return $SimpleStatement1Val;
         |}
         |""".stripMargin
  }

  test("traverse() for one statement, shouldReturnValue=No") {
    doWrite(
      s"""$SimpleStatement1Val;
         |""".stripMargin)
      .when(blockStatTraverser).traverseLast(eqTree(SimpleStatement1), ArgumentMatchers.eq(No))

    blockTraverser.traverse(SimpleStatement1)

    outputWriter.toString shouldBe
      s""" {
         |$SimpleStatement1Val;
         |}
         |""".stripMargin
  }

  test("traverse() for block of one statement, shouldReturnValue=No") {
    doWrite(
      s"""$SimpleStatement1Val;
         |""".stripMargin)
      .when(blockStatTraverser).traverseLast(eqTree(SimpleStatement1), ArgumentMatchers.eq(No))

    blockTraverser.traverse(Block(List(SimpleStatement1)))

    outputWriter.toString shouldBe
      s""" {
         |$SimpleStatement1Val;
         |}
         |""".stripMargin
  }

  test("traverse() for block of two statements, shouldReturnValue=Yes") {
    doWrite(
      s"""$SimpleStatement1Val;
         |""".stripMargin
    ).when(blockStatTraverser).traverse(eqTree(SimpleStatement1))
    doWrite(
      s"""return $SimpleStatement2Val;
         |""".stripMargin
    ).when(blockStatTraverser).traverseLast(eqTree(SimpleStatement2), ArgumentMatchers.eq(Yes))

    blockTraverser.traverse(Block(List(SimpleStatement1, SimpleStatement2)), shouldReturnValue = Yes)

    outputWriter.toString shouldBe
      s""" {
         |$SimpleStatement1Val;
         |return $SimpleStatement2Val;
         |}
         |""".stripMargin
  }

  test("traverse() for block of two statements, shouldReturnValue=No") {
    doWrite(
      s"""$SimpleStatement1Val;
         |""".stripMargin
    ).when(blockStatTraverser).traverse(eqTree(SimpleStatement1))
    doWrite(
      s"""$SimpleStatement2Val;
         |""".stripMargin
    ).when(blockStatTraverser).traverseLast(eqTree(SimpleStatement2), ArgumentMatchers.eq(No))

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


  test("traverse() for an 'init' and one statement") {
    val init = Init(
      tpe = Type.Singleton(ref = This(Name.Anonymous())),
      name = Name.Anonymous(),
      argss = List(List(Lit.String("dummy")))
    )
    val initVal = "this(dummy)"

    doWrite(initVal).when(initTraverser).traverse(eqTree(init), ArgumentMatchers.eq(false))
    doWrite(
      s"""$SimpleStatement1Val;
         |""".stripMargin)
      .when(blockStatTraverser).traverseLast(eqTree(SimpleStatement1), ArgumentMatchers.eq(No))

    blockTraverser.traverse(stat = Block(List(SimpleStatement1)), maybeInit = Some(init))

    outputWriter.toString shouldBe
      s""" {
         |$initVal;
         |$SimpleStatement1Val;
         |}
         |""".stripMargin
  }
}
