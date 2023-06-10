package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ArgumentContext, ArgumentListContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentContextMatcher.eqArgumentContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, XtensionQuasiquoteTerm}

class ArgumentListRendererImplTest extends UnitTestSuite {

  private val arg1 = Term.Name("arg1")
  private val arg2 = Term.Name("arg2")
  private val arg3 = Term.Name("arg3")

  private val argumentRenderer1 = mock[ArgumentRenderer[Term]]
  private val argumentRenderer2 = mock[ArgumentRenderer[Term]]
  private val argumentRenderer3 = mock[ArgumentRenderer[Term]]

  private val argRendererProvider = (idx: Int) => idx match {
    case 0 => argumentRenderer1
    case 1 => argumentRenderer2
    case _ => argumentRenderer3
  }

  private val argumentListRenderer = new ArgumentListRendererImpl()


  test("render() when one arg, multi-line and no wrapping delimiter") {
    doWrite("arg1").when(argumentRenderer1).render(eqTree(arg1), eqArgumentContext(ArgumentContext()))

    argumentListRenderer.render(args = List(arg1), argRendererProvider = argRendererProvider)

    outputWriter.toString shouldBe "arg1"
  }

  test("render() when two args, multi-line and no wrapping delimiter") {
    doWrite("arg1").when(argumentRenderer1).render(eqTree(arg1), eqArgumentContext(ArgumentContext()))
    doWrite("arg2").when(argumentRenderer2).render(eqTree(arg2), eqArgumentContext(ArgumentContext()))

    argumentListRenderer.render(args = List(arg1, arg2), argRendererProvider = argRendererProvider)

    outputWriter.toString shouldBe "arg1, arg2"
  }

  test("render() when three args, multi-line and no wrapping delimiter") {
    doWrite("arg1").when(argumentRenderer1).render(eqTree(arg1), eqArgumentContext(ArgumentContext()))
    doWrite("arg2").when(argumentRenderer2).render(eqTree(arg2), eqArgumentContext(ArgumentContext()))
    doWrite("arg3").when(argumentRenderer3).render(eqTree(arg3), eqArgumentContext(ArgumentContext()))

    argumentListRenderer.render(
      args = List(arg1, arg2, arg3),
      argRendererProvider = argRendererProvider
    )

    outputWriter.toString shouldBe
      """arg1,
        |arg2,
        |arg3""".stripMargin
  }

  test("render() when one arg, single-line and no wrapping delimiter") {
    doWrite("arg1").when(argumentRenderer1).render(eqTree(arg1), eqArgumentContext(ArgumentContext()))

    argumentListRenderer.render(
      args = List(arg1),
      argRendererProvider = argRendererProvider,
      ArgumentListContext(options = ListTraversalOptions(onSameLine = true))
    )

    outputWriter.toString shouldBe "arg1"
  }

  test("render() when two args, single-line and no wrapping delimiter") {
    doWrite("arg1").when(argumentRenderer1).render(eqTree(arg1), eqArgumentContext(ArgumentContext()))
    doWrite("arg2").when(argumentRenderer2).render(eqTree(arg2), eqArgumentContext(ArgumentContext()))

    argumentListRenderer.render(
      args = List(arg1, arg2),
      argRendererProvider = argRendererProvider,
      ArgumentListContext(options = ListTraversalOptions(onSameLine = true))
    )

    outputWriter.toString shouldBe "arg1, arg2"
  }

  test("render() when three args, single-line and no wrapping delimiter") {
    doWrite("arg1").when(argumentRenderer1).render(eqTree(arg1), eqArgumentContext(ArgumentContext()))
    doWrite("arg2").when(argumentRenderer2).render(eqTree(arg2), eqArgumentContext(ArgumentContext()))
    doWrite("arg3").when(argumentRenderer3).render(eqTree(arg3), eqArgumentContext(ArgumentContext()))

    argumentListRenderer.render(
      args = List(arg1, arg2, arg3),
      argRendererProvider = argRendererProvider,
      ArgumentListContext(options = ListTraversalOptions(onSameLine = true))
    )

    outputWriter.toString shouldBe "arg1, arg2, arg3"
  }

  test("render() when no args, traverseEmpty=false, single-line and parentheses") {
    argumentListRenderer.render(
      args = List.empty,
      argRendererProvider = argRendererProvider,
      ArgumentListContext(options = ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses)))
    )

    outputWriter.toString shouldBe ""

    verifyNoMoreInteractions(argumentRenderer1)
  }

  test("render() when no args, traverseEmpty=true, single-line and parentheses") {
    argumentListRenderer.render(
      args = List.empty,
      argRendererProvider = argRendererProvider,
      ArgumentListContext(options = ListTraversalOptions(
        onSameLine = true,
        maybeEnclosingDelimiter = Some(Parentheses),
        traverseEmpty = true)
      )
    )

    outputWriter.toString shouldBe "()"

    verifyNoMoreInteractions(argumentRenderer1)
  }

  test("render() when one arg, single-line and parentheses") {
    doWrite("arg1").when(argumentRenderer1).render(eqTree(arg1), eqArgumentContext(ArgumentContext()))

    argumentListRenderer.render(
      args = List(arg1),
      argRendererProvider = argRendererProvider,
      ArgumentListContext(options = ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses)))
    )

    outputWriter.toString shouldBe "(arg1)"
  }

  test("render() when two args, single-line and parentheses") {
    doWrite("arg1").when(argumentRenderer1).render(eqTree(arg1), eqArgumentContext(ArgumentContext()))
    doWrite("arg2").when(argumentRenderer2).render(eqTree(arg2), eqArgumentContext(ArgumentContext()))

    argumentListRenderer.render(
      args = List(arg1, arg2),
      argRendererProvider = argRendererProvider,
      ArgumentListContext(options = ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses)))
    )

    outputWriter.toString shouldBe "(arg1, arg2)"
  }

  test("render() when three args, single-line and parentheses") {
    doWrite("arg1").when(argumentRenderer1).render(eqTree(arg1), eqArgumentContext(ArgumentContext()))
    doWrite("arg2").when(argumentRenderer2).render(eqTree(arg2), eqArgumentContext(ArgumentContext()))
    doWrite("arg3").when(argumentRenderer3).render(eqTree(arg3), eqArgumentContext(ArgumentContext()))

    argumentListRenderer.render(
      args = List(arg1, arg2, arg3),
      argRendererProvider = argRendererProvider,
      ArgumentListContext(options = ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses)))
    )

    outputWriter.toString shouldBe "(arg1, arg2, arg3)"
  }

  test("render() when one arg and has parent") {
    val methodInvocation = q"myMethod(arg1)"

    doWrite("arg1")
      .when(argumentRenderer1).render(
      eqTree(arg1),
      eqArgumentContext(ArgumentContext())
    )

    argumentListRenderer.render(
      args = List(arg1),
      argRendererProvider = argRendererProvider,
      ArgumentListContext(
        options = ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses))
      )
    )

    outputWriter.toString shouldBe "(arg1)"
  }

  test("render() when one arg and argNameAsComment=true") {
    doWrite("arg1")
      .when(argumentRenderer1).render(
      eqTree(arg1),
      eqArgumentContext(ArgumentContext(argNameAsComment = true))
    )

    argumentListRenderer.render(
      args = List(arg1),
      argRendererProvider = argRendererProvider,
      ArgumentListContext(
        options = ListTraversalOptions(onSameLine = true, maybeEnclosingDelimiter = Some(Parentheses)),
        argNameAsComment = true
      )
    )

    outputWriter.toString shouldBe "(arg1)"
  }
}
