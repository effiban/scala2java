package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaUnit
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class FunctionTypeInferrerImplTest extends UnitTestSuite {

  private val termTypeInferrer = mock[TermTypeInferrer]

  private val functionTypeInferrer = new FunctionTypeInferrerImpl(termTypeInferrer)

  test("infer() for function of () => Unit") {
    val termFunction = q"""() => print("bla")"""

    when(termTypeInferrer.infer(eqTree(q"""print("bla")"""))).thenReturn(Some(ScalaUnit))

    functionTypeInferrer.infer(termFunction).structure shouldBe t"() => scala.Unit".structure
  }

  test("infer() for function of () => Any") {
    val termFunction = q"""() => doSomething()"""

    when(termTypeInferrer.infer(eqTree(q"doSomething()"))).thenReturn(None)

    functionTypeInferrer.infer(termFunction).structure shouldBe t"() => Any".structure
  }

  test("infer() for function of Int => Int") {
    val termFunction = q"(x: Int) => x + 1"

    when(termTypeInferrer.infer(eqTree(q"x + 1"))).thenReturn(Some(TypeNames.Int))

    functionTypeInferrer.infer(termFunction).structure shouldBe t"Int => Int".structure
  }

  test("infer() for function of Int => Unit") {
    val termFunction = q"(x: Int) => print(x)"

    when(termTypeInferrer.infer(eqTree(q"print(x)"))).thenReturn(Some(ScalaUnit))

    functionTypeInferrer.infer(termFunction).structure shouldBe t"Int => scala.Unit".structure
  }

  test("infer() for function of Int => Any") {
    val termFunction = q"(x: Int) => doSomething(x)"

    when(termTypeInferrer.infer(eqTree(q"doSomething(x)"))).thenReturn(None)

    functionTypeInferrer.infer(termFunction).structure shouldBe t"Int => Any".structure
  }

  test("infer() for function of (Int, String) => String") {
    val termFunction = q"""(x: Int, y: String) => "bla" + x + y"""

    when(termTypeInferrer.infer(eqTree(q""""bla" + x + y"""))).thenReturn(Some(TypeNames.String))

    functionTypeInferrer.infer(termFunction).structure shouldBe t"(Int, String) => String".structure
  }
}
