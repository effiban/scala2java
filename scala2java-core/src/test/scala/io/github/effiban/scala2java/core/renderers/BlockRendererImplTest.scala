package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, InitContext}
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.Term.Block
import scala.meta.{XtensionQuasiquoteInit, XtensionQuasiquoteTerm}

class BlockRendererImplTest extends UnitTestSuite {
  private val blockTermRenderer = mock[BlockTermRenderer]
  private val initRenderer = mock[InitRenderer]

  private val blockRenderer = new BlockRendererImpl(blockTermRenderer, initRenderer)


  test("render() when block is empty") {
    blockRenderer.render(Block(List.empty))

    outputWriter.toString shouldBe
      """ {
        |}
        |""".stripMargin
  }

  test("render() for block of one term, uncertainReturn=false") {
    val block =
      q"""
      {
        foo()
      }
      """
    doWrite(
      s"""  foo();
         |""".stripMargin)
      .when(blockTermRenderer).renderLast(eqTree(q"foo()"), uncertainReturn = eqTo(false))

    blockRenderer.render(block = block)

    outputWriter.toString shouldBe
      s""" {
         |  foo();
         |}
         |""".stripMargin
  }

  test("render() for block of one term, uncertainReturn=true") {
    val block =
      q"""
      {
        foo()
      }
      """
    doWrite(
      s"""  /* return? */foo();
         |""".stripMargin)
      .when(blockTermRenderer).renderLast(eqTree(q"foo()"), uncertainReturn = eqTo(true))

    blockRenderer.render(block = block, context = BlockRenderContext(uncertainReturn = true))

    outputWriter.toString shouldBe
      s""" {
         |  /* return? */foo();
         |}
         |""".stripMargin
  }

  test("render() for block of two terms, uncertainReturn=false") {
    val block =
      q"""
        func1()
        func2()
      """
    doWrite(
      s"""  func1();
         |""".stripMargin
    ).when(blockTermRenderer).render(eqTree(q"func1()"))
    doWrite(
      s"""  func2();
         |""".stripMargin
    ).when(blockTermRenderer).renderLast(eqTree(q"func2()"), uncertainReturn = eqTo(false))

    blockRenderer.render(block = block, context = BlockRenderContext())

    outputWriter.toString shouldBe
      s""" {
         |  func1();
         |  func2();
         |}
         |""".stripMargin
  }

  test("render() for block of two terms, uncertainReturn=true") {
    val block =
      q"""
    func1()
    func2()
  """
    doWrite(
      s"""  func1();
         |""".stripMargin
    ).when(blockTermRenderer).render(eqTree(q"func1()"))
    doWrite(
      s"""  /* return? */func2();
         |""".stripMargin
    ).when(blockTermRenderer).renderLast(eqTree(q"func2()"), uncertainReturn = eqTo(true))

    blockRenderer.render(block = block, context = BlockRenderContext(uncertainReturn = true))

    outputWriter.toString shouldBe
      s""" {
         |  func1();
         |  /* return? */func2();
         |}
         |""".stripMargin
  }

  test("render() for an 'init' and one statement") {
    val block =
      q"""
      {
        foo()
      }
      """
    val init = init"this(dummy)"
    val initStr = "  this(dummy)"

    doWrite(initStr)
      .when(initRenderer).render(eqTree(init), ArgumentMatchers.eq(InitContext(argNameAsComment = true)))
    doWrite(
      s"""  foo();
         |""".stripMargin)
      .when(blockTermRenderer).renderLast(eqTree(q"foo()"), uncertainReturn = eqTo(false))

    blockRenderer.render(block = block, context = BlockRenderContext(maybeInit = Some(init)))

    outputWriter.toString shouldBe
      s""" {
         |  this(dummy);
         |  foo();
         |}
         |""".stripMargin
  }

}
