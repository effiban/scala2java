package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, IfRenderContext, SimpleBlockStatRenderContext}
import io.github.effiban.scala2java.core.matchers.BlockRenderContextMockitoMatcher.eqBlockRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.{Apply, ApplyInfix, Block, If}
import scala.meta.{Lit, Term}

class IfRendererImplTest extends UnitTestSuite {
  private val expressionTermRenderer = mock[ExpressionTermRenderer]
  private val blockRenderer = mock[BlockRenderer]
  private val defaultTermRenderer = mock[DefaultTermRenderer]

  private val ifRenderer = new IfRendererImpl(
    expressionTermRenderer,
    blockRenderer,
    defaultTermRenderer
  )


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
  private val ThenStatementStr = "operation1(x)"

  private val ElseBlock = Block(
    List(
      Apply(otherOperation1, List(x)),
      Apply(otherOperation2, List(y))
    )
  )

  private val ElseStatement = Apply(otherOperation1, List(x))
  private val ElseStatementStr = "otherOperation1(x)"


  test("traverse() when 'then' is a block, no 'else', and no uncertain returns") {
    val `if` = If(
      cond = Condition,
      thenp = ThenBlock,
      elsep = Lit.Unit()
    )

    doWrite("x < 3").when(expressionTermRenderer).render(eqTree(Condition))
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(ThenBlock), context = eqBlockRenderContext(BlockRenderContext()))

    ifRenderer.render(`if`)

    outputWriter.toString shouldBe
      """if (x < 3) {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse() when 'then' is a block, no 'else', and has uncertain return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenBlock,
      elsep = Lit.Unit()
    )

    val ifContext = IfRenderContext(
      thenContext = BlockRenderContext(lastStatContext = SimpleBlockStatRenderContext(uncertainReturn = true))
    )

    doWrite("x < 3").when(expressionTermRenderer).render(eqTree(Condition))
    doWrite(
      """ {
        |  /* BODY */
        |  /* return? */last
        |}
        |""".stripMargin).when(blockRenderer).render(
      block = eqTree(ThenBlock),
      context = eqBlockRenderContext(ifContext.thenContext)
    )

    ifRenderer.render(`if`, ifContext)

    outputWriter.toString shouldBe
      """if (x < 3) {
        |  /* BODY */
        |  /* return? */last
        |}
        |""".stripMargin
  }

  test("traverse() when 'then' is a single statement, and no 'else'") {
    val `if` = If(
      cond = Condition,
      thenp = ThenStatement,
      elsep = Lit.Unit()
    )

    doWrite("x < 3").when(expressionTermRenderer).render(eqTree(Condition))
    doWrite(ThenStatementStr).when(defaultTermRenderer).render(eqTree(ThenStatement))

    ifRenderer.render(`if`)

    outputWriter.toString shouldBe
      s"if (x < 3) $ThenStatement"
  }

  test("traverse() when 'else' is a block, and no uncertain return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenBlock,
      elsep = ElseBlock
    )

    doWrite("x < 3").when(expressionTermRenderer).render(eqTree(Condition))
    doWrite(
      """ {
        |  /* THEN BODY */
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(ThenBlock), context = eqBlockRenderContext(BlockRenderContext()))
    doWrite(
      """ {
        |  /* ELSE BODY */
        |}
        |""".stripMargin)
      .when(blockRenderer).render(block = eqTree(ElseBlock), context = eqBlockRenderContext(BlockRenderContext()))

    ifRenderer.render(`if`)

    outputWriter.toString shouldBe
      """if (x < 3) {
        |  /* THEN BODY */
        |}
        |else {
        |  /* ELSE BODY */
        |}
        |""".stripMargin
  }

  test("traverse() when 'else' is a block, and 'then' has uncertain return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenBlock,
      elsep = ElseBlock
    )

    val thenContext = BlockRenderContext(lastStatContext = SimpleBlockStatRenderContext(uncertainReturn = true))
    val ifContext = IfRenderContext(thenContext = thenContext)

    doWrite("x < 3").when(expressionTermRenderer).render(eqTree(Condition))
    doWrite(
      """ {
        |  /* THEN BODY */
        |  /* return? */last
        |}
        |""".stripMargin).when(blockRenderer).render(
      block = eqTree(ThenBlock), context = eqBlockRenderContext(thenContext)
    )
    doWrite(
      """ {
        |  /* ELSE BODY */
        |  last
        |}
        |""".stripMargin).when(blockRenderer).render(
      block = eqTree(ElseBlock), context = eqBlockRenderContext(BlockRenderContext())
    )

    ifRenderer.render(`if`, ifContext)

    outputWriter.toString shouldBe
      """if (x < 3) {
        |  /* THEN BODY */
        |  /* return? */last
        |}
        |else {
        |  /* ELSE BODY */
        |  last
        |}
        |""".stripMargin
  }

  test("traverse() when 'else' is a block, and 'else' has uncertain return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenBlock,
      elsep = ElseBlock
    )

    val elseContext = BlockRenderContext(lastStatContext = SimpleBlockStatRenderContext(uncertainReturn = true))
    val ifContext = IfRenderContext(elseContext = elseContext)

    doWrite("x < 3").when(expressionTermRenderer).render(eqTree(Condition))
    doWrite(
      """ {
        |  /* THEN BODY */
        |  last
        |}
        |""".stripMargin).when(blockRenderer).render(
      block = eqTree(ThenBlock), context = eqBlockRenderContext(BlockRenderContext())
    )
    doWrite(
      """ {
        |  /* ELSE BODY */
        |  /* return? */last
        |}
        |""".stripMargin).when(blockRenderer).render(
      block = eqTree(ElseBlock), context = eqBlockRenderContext(elseContext)
    )

    ifRenderer.render(`if`, ifContext)

    outputWriter.toString shouldBe
      """if (x < 3) {
        |  /* THEN BODY */
        |  last
        |}
        |else {
        |  /* ELSE BODY */
        |  /* return? */last
        |}
        |""".stripMargin
  }

  test("traverse() when 'else' is a block, and both clauses have uncertain return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenBlock,
      elsep = ElseBlock
    )

    val clauseContext = BlockRenderContext(lastStatContext = SimpleBlockStatRenderContext(uncertainReturn = true))
    val ifContext = IfRenderContext(thenContext = clauseContext, elseContext = clauseContext)

    doWrite("x < 3").when(expressionTermRenderer).render(eqTree(Condition))
    doWrite(
      """ {
        |  /* THEN BODY */
        |  /* return? */last
        |}
        |""".stripMargin).when(blockRenderer).render(
      block = eqTree(ThenBlock), context = eqBlockRenderContext(clauseContext)
    )
    doWrite(
      """ {
        |  /* ELSE BODY */
        |  /* return? */last
        |}
        |""".stripMargin).when(blockRenderer).render(
      block = eqTree(ElseBlock), context = eqBlockRenderContext(clauseContext)
    )

    ifRenderer.render(`if`, ifContext)

    outputWriter.toString shouldBe
      """if (x < 3) {
        |  /* THEN BODY */
        |  /* return? */last
        |}
        |else {
        |  /* ELSE BODY */
        |  /* return? */last
        |}
        |""".stripMargin
  }

  test("traverse() when 'else' is a single statement, and no uncertain return") {
    val `if` = If(
      cond = Condition,
      thenp = ThenBlock,
      elsep = ElseStatement
    )

    doWrite("x < 3").when(expressionTermRenderer).render(eqTree(Condition))
    doWrite(
      """ {
        |  /* THEN BODY */
        |}
        |""".stripMargin).when(blockRenderer).render(
      block = eqTree(ThenBlock), context = eqBlockRenderContext(BlockRenderContext())
    )
    doWrite(ElseStatementStr).when(defaultTermRenderer).render(eqTree(ElseStatement))

    ifRenderer.render(`if`)

    outputWriter.toString shouldBe
      s"""if (x < 3) {
         |  /* THEN BODY */
         |}
         |else $ElseStatementStr""".stripMargin
  }

  test("traverseAsTertiaryOp()") {
    val lessThanThree = Lit.String("LessThanThree")
    val threeOrMore = Lit.String("ThreeOrMore")

    val `if` = If(
      cond = Condition,
      thenp = lessThanThree,
      elsep = threeOrMore
    )

    doWrite("x < 3").when(expressionTermRenderer).render(eqTree(Condition))
    doWrite(""""LessThanThree"""").when(expressionTermRenderer).render(eqTree(lessThanThree))
    doWrite(""""ThreeOrMore"""").when(expressionTermRenderer).render(eqTree(threeOrMore))

    ifRenderer.renderAsTertiaryOp(`if`)

    outputWriter.toString shouldBe """(x < 3) ? "LessThanThree" : "ThreeOrMore""""
  }

}
