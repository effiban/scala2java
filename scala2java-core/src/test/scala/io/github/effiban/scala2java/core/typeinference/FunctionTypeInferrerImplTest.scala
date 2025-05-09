package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TypeSelects
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class FunctionTypeInferrerImplTest extends UnitTestSuite {

  private val termTypeInferrer = mock[TermTypeInferrer]

  private val functionTypeInferrer = new FunctionTypeInferrerImpl(termTypeInferrer)

  test("infer() for function of () => scala.Unit") {
    val termFunction = q"""() => print("bla")"""

    when(termTypeInferrer.infer(eqTree(q"""print("bla")"""))).thenReturn(Some(TypeSelects.ScalaUnit))

    functionTypeInferrer.infer(termFunction).structure shouldBe t"() => scala.Unit".structure
  }

  test("infer() for function of () => scala.Any") {
    val termFunction = q"""() => doSomething()"""

    when(termTypeInferrer.infer(eqTree(q"doSomething()"))).thenReturn(None)

    functionTypeInferrer.infer(termFunction).structure shouldBe t"() => scala.Any".structure
  }

  test("infer() for function of scala.Int => scala.Int") {
    val termFunction = q"(x: scala.Int) => x + 1"

    when(termTypeInferrer.infer(eqTree(q"x + 1"))).thenReturn(Some(TypeSelects.ScalaInt))

    functionTypeInferrer.infer(termFunction).structure shouldBe t"scala.Int => scala.Int".structure
  }

  test("infer() for function of scala.Int => scala.Unit") {
    val termFunction = q"(x: scala.Int) => print(x)"

    when(termTypeInferrer.infer(eqTree(q"print(x)"))).thenReturn(Some(TypeSelects.ScalaUnit))

    functionTypeInferrer.infer(termFunction).structure shouldBe t"scala.Int => scala.Unit".structure
  }

  test("infer() for function of scala.Int => scala.Any") {
    val termFunction = q"(x: scala.Int) => doSomething(x)"

    when(termTypeInferrer.infer(eqTree(q"doSomething(x)"))).thenReturn(None)

    functionTypeInferrer.infer(termFunction).structure shouldBe t"scala.Int => scala.Any".structure
  }

  test("infer() for function of (scala.Int, java.lang.String) => java.lang.String") {
    val termFunction = q"""(x: scala.Int, y: java.lang.String) => "bla" + x + y"""

    when(termTypeInferrer.infer(eqTree(q""""bla" + x + y"""))).thenReturn(Some(TypeSelects.JavaString))

    functionTypeInferrer.infer(termFunction).structure shouldBe t"(scala.Int, java.lang.String) => java.lang.String".structure
  }
}
