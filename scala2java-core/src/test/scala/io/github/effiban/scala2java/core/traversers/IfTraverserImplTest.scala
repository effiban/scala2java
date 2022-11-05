package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.BlockContext
import io.github.effiban.scala2java.core.entities.Decision.Yes
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Term.{Apply, ApplyInfix, Block, If}
import scala.meta.{Lit, Term}

class IfTraverserImplTest extends UnitTestSuite {

  private val termTraverser = mock[TermTraverser]
  private val blockTraverser = mock[BlockTraverser]

  private val ifTraverser = new IfTraverserImpl(termTraverser, blockTraverser)

  private val x = Term.Name("x")
  private val y = Term.Name("y")

  private val operation1: Term.Name = Term.Name("operation1")
  private val operation2: Term.Name = Term.Name("operation2")
  private val otherOperation1: Term.Name = Term.Name("otherOperation1")
  private val otherOperation2: Term.Name = Term.Name("otherOperation2")

  private val Condition = ApplyInfix(
    lhs = x,
    op = Term.Name("<"),
    targs = Nil,
    args = List(Lit.Int(3))
  )

  private val ThenBlock = Block(
    List(
      Apply(operation1, List(x)),
      Apply(operation2, List(y))
    )
  )

  private val ThenStatement = Apply(operation1, List(x))

  private val ElseBlock = Block(
    List(
      Apply(otherOperation1, List(x)),
      Apply(otherOperation2, List(y))
    )
  )

  private val ElseStatement = Apply(otherOperation1, List(x))


  test("traverse() when 'then' is a block, no 'else', and should not return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenBlock,
      elsep = Lit.Unit()
    )

    doWrite("x < 3").when(termTraverser).traverse(eqTree(Condition))
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(ThenBlock), context = eqBlockContext(BlockContext()))

    ifTraverser.traverse(`if`)

    outputWriter.toString shouldBe
      """if (x < 3) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() when 'then' is a block, no 'else', and should return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenBlock,
      elsep = Lit.Unit()
    )

    doWrite("x < 3").when(termTraverser).traverse(eqTree(Condition))
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin).when(blockTraverser).traverse(
      stat = eqTree(ThenBlock),
      context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )

    ifTraverser.traverse(`if`, shouldReturnValue = true)

    outputWriter.toString shouldBe
      """if (x < 3) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() when 'then' is a single statement, no 'else' and should not return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenStatement,
      elsep = Lit.Unit()
    )

    doWrite("x < 3").when(termTraverser).traverse(eqTree(Condition))
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(ThenStatement), context = eqBlockContext(BlockContext()))

    ifTraverser.traverse(`if`)

    outputWriter.toString shouldBe
      """if (x < 3) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() when 'else' is a block, and should not return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenStatement,
      elsep = ElseBlock
    )

    doWrite("x < 3").when(termTraverser).traverse(eqTree(Condition))
    doWrite(
      """ {
        |  /* THEN BODY */
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(ThenStatement), context = eqBlockContext(BlockContext()))
    doWrite(
      """ {
        |  /* ELSE BODY */
        |}
        |""".stripMargin)
      .when(blockTraverser).traverse(stat = eqTree(ElseBlock), context = eqBlockContext(BlockContext()))

    ifTraverser.traverse(`if`)

    outputWriter.toString shouldBe
      """if (x < 3) {
        |  /* THEN BODY */
        |}
        |else {
        |  /* ELSE BODY */
        |}
        |""".stripMargin
  }

  test("traverse() when 'else' is a block, and should return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenStatement,
      elsep = ElseBlock
    )

    doWrite("x < 3").when(termTraverser).traverse(eqTree(Condition))
    doWrite(
      """ {
        |  /* THEN BODY */
        |}
        |""".stripMargin).when(blockTraverser).traverse(
      stat = eqTree(ThenStatement), context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )
    doWrite(
      """ {
        |  /* ELSE BODY */
        |}
        |""".stripMargin).when(blockTraverser).traverse(
      stat = eqTree(ElseBlock), context = eqBlockContext(BlockContext(shouldReturnValue = Yes))
    )

    ifTraverser.traverse(`if`, shouldReturnValue = true)

    """if (x < 3) {
      |  /* THEN BODY */
      |}
      |else {
      |  /* ELSE BODY */
      |}
      |""".stripMargin
  }

  test("traverse() when 'else' is a single statement, and should not return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenStatement,
      elsep = ElseStatement
    )

    doWrite("x < 3").when(termTraverser).traverse(eqTree(Condition))
    doWrite(
      """ {
        |  /* THEN BODY */
        |}
        |""".stripMargin).when(blockTraverser).traverse(
      stat = eqTree(ThenStatement), context = eqBlockContext(BlockContext())
    )
    doWrite(
      """ {
        |  /* ELSE BODY */
        |}
        |""".stripMargin).when(blockTraverser).traverse(
      stat = eqTree(ElseStatement), context = eqBlockContext(BlockContext())
    )

    ifTraverser.traverse(`if`)

    outputWriter.toString shouldBe
      """if (x < 3) {
        |  /* THEN BODY */
        |}
        |else {
        |  /* ELSE BODY */
        |}
        |""".stripMargin
  }

  test("traverseAsTertiaryOp()") {
    val lessThanThree = Lit.String("LessThanThree")
    val threeOrMore = Lit.String("ThreeOrMore")

    val `if` = If(
      cond = Condition,
      thenp = lessThanThree,
      elsep = threeOrMore
    )

    doWrite("x < 3").when(termTraverser).traverse(eqTree(Condition))
    doWrite(""""LessThanThree"""").when(termTraverser).traverse(eqTree(lessThanThree))
    doWrite(""""ThreeOrMore"""").when(termTraverser).traverse(eqTree(threeOrMore))

    ifTraverser.traverseAsTertiaryOp(`if`)

    outputWriter.toString shouldBe """(x < 3) ? "LessThanThree" : "ThreeOrMore""""
  }
}
