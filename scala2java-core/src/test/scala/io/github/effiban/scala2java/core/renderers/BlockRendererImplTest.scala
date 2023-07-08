package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, BlockStatRenderContext}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Term.Block
import scala.meta.XtensionQuasiquoteTerm

class BlockRendererImplTest extends UnitTestSuite {
  private val blockStatRenderer = mock[BlockStatRenderer]

  private val blockRenderer = new BlockRendererImpl(blockStatRenderer)


  test("render() when block is empty") {
    blockRenderer.render(Block(List.empty))

    outputWriter.toString shouldBe
      """ {
        |}
        |""".stripMargin
  }

  test("render() for block of one simple statement, uncertainReturn=false") {
    val block =
      q"""
      {
        foo()
      }
      """
    doWrite(
      s"""  foo();
         |""".stripMargin)
      .when(blockStatRenderer).renderLast(eqTree(q"foo()"), eqTo(BlockStatRenderContext()))

    blockRenderer.render(block = block)

    outputWriter.toString shouldBe
      s""" {
         |  foo();
         |}
         |""".stripMargin
  }

  test("render() for block of one simple statement, uncertainReturn=true") {
    val block =
      q"""
      {
        foo()
      }
      """
    val lastStatContext = BlockStatRenderContext(uncertainReturn = true)

    doWrite(
      s"""  /* return? */foo();
         |""".stripMargin)
      .when(blockStatRenderer).renderLast(eqTree(q"foo()"), eqTo(lastStatContext))

    blockRenderer.render(block = block, context = BlockRenderContext(uncertainReturn = true))

    outputWriter.toString shouldBe
      s""" {
         |  /* return? */foo();
         |}
         |""".stripMargin
  }

  test("render() for block of two simple statements, uncertainReturn=false") {
    val block =
      q"""
        func1()
        func2()
      """
    doWrite(
      s"""  func1();
         |""".stripMargin
    ).when(blockStatRenderer).render(eqTree(q"func1()"))
    doWrite(
      s"""  func2();
         |""".stripMargin
    ).when(blockStatRenderer).renderLast(eqTree(q"func2()"), eqTo(BlockStatRenderContext()))

    blockRenderer.render(block = block, context = BlockRenderContext())

    outputWriter.toString shouldBe
      s""" {
         |  func1();
         |  func2();
         |}
         |""".stripMargin
  }

  test("render() for block of two simple statements, uncertainReturn=true") {
    val block =
      q"""
      func1()
      func2()
      """

    val lastStatContext = BlockStatRenderContext(uncertainReturn = true)

    doWrite(
      s"""  func1();
         |""".stripMargin
    ).when(blockStatRenderer).render(eqTree(q"func1()"))
    doWrite(
      s"""  /* return? */func2();
         |""".stripMargin
    ).when(blockStatRenderer).renderLast(eqTree(q"func2()"), eqTo(lastStatContext))

    blockRenderer.render(block = block, context = BlockRenderContext(uncertainReturn = true))

    outputWriter.toString shouldBe
      s""" {
         |  func1();
         |  /* return? */func2();
         |}
         |""".stripMargin
  }

  test("render() for block of one Term.Apply + one If, uncertainReturn=true") {
    val block =
      q"""
      func1()
      if (x < 3) small() else large()
      """

    val lastStatContext = BlockStatRenderContext(uncertainReturn = true)

    doWrite(
      s"""  func1();
         |""".stripMargin
    ).when(blockStatRenderer).render(eqTree(q"func1()"))
    doWrite(
      s"""  if (x < 3) {
         |    /* return? */ small()
         |  } else {
         |    /* return? */ large()
         |  }
         |""".stripMargin
    ).when(blockStatRenderer).renderLast(eqTree(q"if (x < 3) small() else large()"), eqTo(lastStatContext))

    blockRenderer.render(block = block, context = BlockRenderContext(uncertainReturn = true))

    outputWriter.toString shouldBe
      s""" {
         |  func1();
         |  if (x < 3) {
         |    /* return? */ small()
         |  } else {
         |    /* return? */ large()
         |  }
         |}
         |""".stripMargin
  }
}
