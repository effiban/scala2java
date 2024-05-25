package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Term, XtensionQuasiquoteTerm}

class TermApplyInfixTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val termApplyInfixTraverser = new TermApplyInfixTraverserImpl(expressionTermTraverser)

  test("traverse()") {
    val termApplyInfix = q"a fun(b,c)"
    val traversedTermApplyInfix = q"aa fun(bb,cc)"

    doAnswer((arg: Term) => arg match {
      case q"a" => q"aa"
      case q"b" => q"bb"
      case q"c" => q"cc"
      case other => other
    }).when(expressionTermTraverser).traverse(any[Term])

    termApplyInfixTraverser.traverse(termApplyInfix).structure shouldBe traversedTermApplyInfix.structure
  }
}
