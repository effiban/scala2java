package effiban.scala2java.transformers

import effiban.scala2java.entities.TraversalConstants.JavaPlaceholder
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames

import scala.meta.{Name, Pat, Term, Type}

class PatToTermParamTransformerTest extends UnitTestSuite {

  private val X = Term.Name("x")
  private val PatVarX = Pat.Var(X)

  private val Y = Term.Name("y")
  private val PatVarY = Pat.Var(Y)

  test("transform a Pat.Var") {
    val expectedTermParam = termParam(X)

    val actualMaybeTermParam = PatToTermParamTransformer.transform(PatVarX)

    actualMaybeTermParam.value.structure shouldBe expectedTermParam.structure
  }

  test("transform a Pat.Typed") {
    val patTyped = Pat.Typed(PatVarX, TypeNames.String)
    val expectedTermParam = termParam(X, Some(TypeNames.String))

    val actualMaybeTermParam = PatToTermParamTransformer.transform(patTyped)

    actualMaybeTermParam.value.structure shouldBe expectedTermParam.structure
  }

  test("transform a Pat.Wildcard") {
    val expectedTermParam = termParam(Term.Name(JavaPlaceholder))

    val actualMaybeTermParam = PatToTermParamTransformer.transform(Pat.Wildcard())

    actualMaybeTermParam.value.structure shouldBe expectedTermParam.structure
  }

  test("transform a Pat.Tuple") {
    val patTuple = Pat.Tuple(List(PatVarX, PatVarY))

    val actualMaybeTermParam = PatToTermParamTransformer.transform(patTuple)

    actualMaybeTermParam shouldBe None
  }

  private def termParam(name: Name, decltpe: Option[Type] = None): Term.Param = {
    Term.Param(mods = Nil, name = name, decltpe = decltpe, default = None)
  }
}
