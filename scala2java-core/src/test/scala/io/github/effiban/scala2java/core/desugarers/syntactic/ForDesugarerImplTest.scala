package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Enumerator.Generator
import scala.meta.Term.For
import scala.meta.{Term, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm}

class ForDesugarerImplTest extends UnitTestSuite {

  private final val ForEachFunctionName: Term.Name = q"foreach"

  private val patToTermParamDesugarer = mock[PatToTermParamDesugarer]

  private val forDesugarer = spy(new ForDesugarerImpl(patToTermParamDesugarer))

  test("Should call base trait method correctly") {
    val enumerators = List(
      Generator(pat = p"x", rhs = q"xs"),
      Generator(pat = p"y", rhs = q"ys")
    )
    val body = Term.Name("result")
    val `for` = For(enums = enumerators, body = body)

    doReturn(q"dummy(2)").when(forDesugarer).desugar(eqTreeList(enumerators), eqTree(body))

    forDesugarer.desugar(`for`)

    verify(forDesugarer).desugar(eqTreeList(enumerators), eqTree(body))
  }

  test("intermediateFunctionName should be 'foreach'") {
    forDesugarer.intermediateFunctionName.structure shouldBe ForEachFunctionName.structure
  }

  test("finalFunctionName should be 'foreach'") {
    forDesugarer.finalFunctionName.structure shouldBe ForEachFunctionName.structure
  }
}
