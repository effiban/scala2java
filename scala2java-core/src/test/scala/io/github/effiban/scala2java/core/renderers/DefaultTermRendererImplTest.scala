package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.{BlockRenderContext, IfRenderContext, TermFunctionRenderContext, TryRenderContext}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Term, XtensionQuasiquoteTerm}

class DefaultTermRendererImplTest extends UnitTestSuite {

  private val defaultTermRefRenderer = mock[DefaultTermRefRenderer]
  private val termApplyRenderer = mock[TermApplyRenderer]
  private val compositeApplyTypeRenderer = mock[CompositeApplyTypeRenderer]
  private val applyInfixRenderer = mock[TermApplyInfixRenderer]
  private val assignRenderer = mock[AssignRenderer]
  private val returnRenderer = mock[ReturnRenderer]
  private val throwRenderer = mock[ThrowRenderer]
  private val ascribeRenderer = mock[AscribeRenderer]
  private val termAnnotateRenderer = mock[TermAnnotateRenderer]
  private val blockRenderer = mock[BlockRenderer]
  private val ifRenderer = mock[IfRenderer]
  private val matchRenderer = mock[TermMatchRenderer]
  private val tryRenderer = mock[TryRenderer]
  private val tryWithHandlerRenderer = mock[TryWithHandlerRenderer]
  private val termFunctionRenderer = mock[TermFunctionRenderer]
  private val whileRenderer = mock[WhileRenderer]
  private val doRenderer = mock[DoRenderer]
  private val newRenderer = mock[NewRenderer]
  private val newAnonymousRenderer = mock[NewAnonymousRenderer]
  private val termPlaceholderRenderer = mock[TermPlaceholderRenderer]
  private val etaRenderer = mock[EtaRenderer]
  private val litRenderer = mock[LitRenderer]

  private val defaultTermRenderer = new DefaultTermRendererImpl(
    defaultTermRefRenderer,
    termApplyRenderer,
    compositeApplyTypeRenderer,
    applyInfixRenderer,
    assignRenderer,
    returnRenderer,
    throwRenderer,
    ascribeRenderer,
    termAnnotateRenderer,
    blockRenderer,
    ifRenderer,
    matchRenderer,
    tryRenderer,
    tryWithHandlerRenderer,
    termFunctionRenderer,
    whileRenderer,
    doRenderer,
    newRenderer,
    newAnonymousRenderer,
    termPlaceholderRenderer,
    etaRenderer,
    litRenderer
  )

  test("render Term.Name") {
    val termName = q"x"

    defaultTermRenderer.render(termName)

    verify(defaultTermRefRenderer).render(eqTree(termName))
  }

  test("render Term.Apply") {
    val termApply = q"a(x, y)"

    defaultTermRenderer.render(termApply)

    verify(termApplyRenderer).render(eqTree(termApply))
  }

  test("render Term.ApplyType") {
    val applyType = q"a[T]"

    defaultTermRenderer.render(applyType)

    verify(compositeApplyTypeRenderer).render(eqTree(applyType))
  }

  test("render Term.ApplyInfix") {
    val applyInfix = q"a + b"

    defaultTermRenderer.render(applyInfix)

    verify(applyInfixRenderer).render(eqTree(applyInfix))
  }

  test("render Term.Assign") {
    val assign = q"a = 3"

    defaultTermRenderer.render(assign)

    verify(assignRenderer).render(eqTree(assign))
  }

  test("render Term.Return") {
    val `return` = q"return x"
    defaultTermRenderer.render(`return`)
    verify(returnRenderer).render(eqTree(`return`))
  }

  test("render Term.Throw") {
    val `throw` = q"throw new IllegalStateExcpetion()"
    defaultTermRenderer.render(`throw`)
    verify(throwRenderer).render(eqTree(`throw`))
  }

  test("render Ascribe") {
    val ascribe = q"x: Int"
    defaultTermRenderer.render(ascribe)
    verify(ascribeRenderer).render(eqTree(ascribe))
  }

  test("render Term.Annotate") {
    val termAnnotate = q"(x: @MyAnnotation)"
    defaultTermRenderer.render(termAnnotate)
    verify(termAnnotateRenderer).render(eqTree(termAnnotate))
  }

  test("render Block") {
    val block =
      q"""
      {
        x = calcX()
        y = calcY()
        x + y
      }
      """

    defaultTermRenderer.render(block)

    verify(blockRenderer).render(eqTree(block), eqTo(BlockRenderContext()))
  }

  test("render If") {
    val termIf =
      q"""
      if (cond) {
        doSomething()
      } else {
        doSomethingElse()
      }
      """

    defaultTermRenderer.render(termIf)

    verify(ifRenderer).render(eqTree(termIf), eqTo(IfRenderContext()))
  }

  test("render Match") {
    val `match` =
      q"""
      x match {
        case 1 => doOne()
        case 2 => doTwo()
        default => doNothing()
      }
      """

    defaultTermRenderer.render(`match`)

    verify(matchRenderer).render(eqTree(`match`))
  }

  test("render Term.Try") {
    val termTry =
      q"""
      try {
        doSomething()
      } catch {
        case e: IllegalStateException => recover()
      }
      """

    defaultTermRenderer.render(termTry)

    verify(tryRenderer).render(eqTree(termTry), eqTo(TryRenderContext()))
  }

  test("render TryWithHandler") {
    val tryWithHandler =
      q"""
      try {
        doSomething()
      } catch(catchHandler)
      """

    defaultTermRenderer.render(tryWithHandler)

    verify(tryWithHandlerRenderer).render(eqTree(tryWithHandler), eqTo(TryRenderContext()))
  }

  test("render Term.Function") {
    val termFunction = q"x => doSomething(x)"

    defaultTermRenderer.render(termFunction)

    verify(termFunctionRenderer).render(eqTree(termFunction), eqTo(TermFunctionRenderContext()))
  }

  test("render While") {
    val `while` =
      q"""
      while (x > 3) {
        doSomething(x)
      }
      """

    defaultTermRenderer.render(`while`)

    verify(whileRenderer).render(eqTree(`while`))
  }

  test("render Do") {
    val `do` =
      q"""
      do {
        doSomething(x)
      } while (x > 3)
      """

    defaultTermRenderer.render(`do`)

    verify(doRenderer).render(eqTree(`do`))
  }

  test("render New") {
    val `new` = q"new MyClass(3)"

    defaultTermRenderer.render(`new`)

    verify(newRenderer).render(eqTree(`new`))
  }

  test("render NewAnonymous") {
    val newAnonymous = q"new MyTrait { override def foo(x: Int) = x + 1 }"

    defaultTermRenderer.render(newAnonymous)

    verify(newAnonymousRenderer).render(eqTree(newAnonymous))
  }

  test("render Term.Placeholder") {
    defaultTermRenderer.render(Term.Placeholder())

    verify(termPlaceholderRenderer).render(eqTree(Term.Placeholder()))
  }

  test("render Eta") {
    val eta = Term.Eta(q"func")
    defaultTermRenderer.render(eta)
    verify(etaRenderer).render(eqTree(eta))
  }

  test("render Lit") {
    val lit = q"3"

    defaultTermRenderer.render(lit)

    verify(litRenderer).render(eqTree(lit))
  }
}
