package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Enumerator.Generator
import scala.meta.Term.ForYield
import scala.meta.{Term, XtensionQuasiquoteCaseOrPattern, XtensionQuasiquoteTerm}

class ForYieldDesugarerImplTest extends UnitTestSuite {

  private val patToTermParamDesugarer = mock[PatToTermParamDesugarer]

  private val forYieldDesugarer = spy(new ForYieldDesugarerImpl(patToTermParamDesugarer))

  test("Should call base trait method correctly") {
    val enumerators = List(
      Generator(pat = p"x", rhs = q"xs"),
      Generator(pat = p"y", rhs = q"ys")
    )
    val body = Term.Name("result")
    val forYield = ForYield(enums = enumerators, body = body)

    doReturn(q"dummy(2)").when(forYieldDesugarer).desugar(eqTreeList(enumerators), eqTree(body))

    forYieldDesugarer.desugar(forYield)

    verify(forYieldDesugarer).desugar(eqTreeList(enumerators), eqTree(body))
  }


  test("intermediateFunctionName should be 'flatMap'") {
    forYieldDesugarer.intermediateFunctionName.structure shouldBe Term.Name("flatMap").structure
  }

  test("finalFunctionName should be 'map'") {
    forYieldDesugarer.finalFunctionName.structure shouldBe Term.Name("map").structure
  }
}
