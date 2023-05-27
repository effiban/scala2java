package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.predicates.TermNameHasApplyMethod
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class TermApplyFunDesugarerImplTest extends UnitTestSuite {

  private val termNameHasApplyMethod = mock[TermNameHasApplyMethod]
  private val evaluatedTermSelectQualDesugarer = mock[EvaluatedTermSelectQualDesugarer]
  private val termApplyTypeFunDesugarer = mock[TermApplyTypeFunDesugarer]
  private val evaluatedTermDesugarer = mock[EvaluatedTermDesugarer]

  private val termApplyFunDesugarer = new TermApplyFunDesugarerImpl(
    termNameHasApplyMethod,
    evaluatedTermSelectQualDesugarer,
    termApplyTypeFunDesugarer,
    evaluatedTermDesugarer
  )

  test("desugar() when fun is a Term.Name with implicit 'apply()', should add the 'apply()'") {
    val termName = q"MyObject"
    val termApply = q"MyObject(1)"
    val desugaredTermApply = q"MyObject.apply(1)"

    when(termNameHasApplyMethod(eqTree(termName))).thenReturn(true)

    termApplyFunDesugarer.desugar(termApply).structure shouldBe desugaredTermApply.structure
  }

  test("desugar() when fun is a Term.ApplyType of Term.Name, with implicit 'apply()', should add the 'apply()'") {
    val termName = q"MyObject"
    val termApply = q"MyObject[Int](1)"
    val desugaredTermApply = q"MyObject.apply[Int](1)"

    when(termNameHasApplyMethod(eqTree(termName))).thenReturn(true)

    termApplyFunDesugarer.desugar(termApply).structure shouldBe desugaredTermApply.structure
  }

  test("desugar() when fun is a Term.Name with no implicit 'apply()', should return unchanged") {
    val termName = q"myMethod"
    val termApply = q"myMethod(1)"

    when(termNameHasApplyMethod(eqTree(termName))).thenReturn(false)

    termApplyFunDesugarer.desugar(termApply).structure shouldBe termApply.structure
  }

  test("desugar() when fun is a Term.ApplyType of a Term.Name, with no implicit 'apply()', should return unchanged") {
    val termName = q"myMethod"
    val termApply = q"myMethod[Int](1)"

    when(termNameHasApplyMethod(eqTree(termName))).thenReturn(false)

    termApplyFunDesugarer.desugar(termApply).structure shouldBe termApply.structure
  }

  test("desugar() when fun is a Term.ApplyType of a Term.Select, should desugar the ApplyType") {
    val termApplyType = q"(func(func2)).myMethod[Int]"
    val desugaredTermApplyType = q"(func(func2())).myMethod[Int]"

    val termApply = q"(func(func2)).myMethod[Int]()"
    val desugaredTermApply = q"(func(func2())).myMethod[Int]()"

    doReturn(desugaredTermApplyType).when(termApplyTypeFunDesugarer).desugar(eqTree(termApplyType))

    termApplyFunDesugarer.desugar(termApply).structure shouldBe desugaredTermApply.structure
  }

  test("desugar() when fun is a qualified name, should desugar the qualifier part") {
    val termSelect = q"(func(func2)).myMethod"
    val desugaredTermSelect = q"(func(func2())).myMethod"

    val termApply = q"(func(func2)).myMethod(1)"
    val desugaredTermApply = q"(func(func2())).myMethod(1)"

    doReturn(desugaredTermSelect).when(evaluatedTermSelectQualDesugarer).desugar(eqTree(termSelect))

    termApplyFunDesugarer.desugar(termApply).structure shouldBe desugaredTermApply.structure
  }


  test("desugar() when fun is a Term.Function (lambda) invocation should desugar the lambda and add 'apply()'") {
    val lambdaWithApply = q"((x: Int) => x + func).apply"
    val desugaredLambdaWithApply = q"((x: Int) => x + func()).apply"

    val lambdaInvocation = q"((x: Int) => x + func)(2)"
    val desugaredLambdaInvocation = q"((x: Int) => x + func()).apply(2)"

    doReturn(desugaredLambdaWithApply).when(evaluatedTermSelectQualDesugarer).desugar(eqTree(lambdaWithApply))

    termApplyFunDesugarer.desugar(lambdaInvocation).structure shouldBe desugaredLambdaInvocation.structure
  }

  test("desugar() when fun is a 'If' should desugar it") {
    val fun = q"if (flag) then func1 else func2"
    val desugaredFun = q"if (flag) then func1() else func2()"

    val termApply = q"(if (flag) then func1 else func2)(3)"
    val desugaredTermApply = q"(if (flag) then func1() else func2())(3)"

    doReturn(desugaredFun).when(evaluatedTermDesugarer).desugar(eqTree(fun))

    termApplyFunDesugarer.desugar(termApply).structure shouldBe desugaredTermApply.structure
  }
}
