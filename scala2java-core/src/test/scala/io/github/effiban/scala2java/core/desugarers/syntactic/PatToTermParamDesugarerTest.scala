package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames

import scala.meta.{Name, Pat, Term, Type}

class PatToTermParamDesugarerTest extends UnitTestSuite {

  private val X = Term.Name("x")
  private val PatVarX = Pat.Var(X)

  test("transform a Pat.Var with no default declared type") {
    val expectedTermParam = termParam(X)

    val actualMaybeTermParam = PatToTermParamDesugarer.desugar(PatVarX)

    actualMaybeTermParam.value.structure shouldBe expectedTermParam.structure
  }

  test("transform a Pat.Typed") {
    val patTyped = Pat.Typed(PatVarX, TypeNames.String)
    val expectedTermParam = termParam(X, Some(TypeNames.String))

    val actualMaybeTermParam = PatToTermParamDesugarer.desugar(patTyped)

    actualMaybeTermParam.value.structure shouldBe expectedTermParam.structure
  }

  test("transform a Pat.Wildcard") {
    PatToTermParamDesugarer.desugar(Pat.Wildcard()) shouldBe None
  }

  private def termParam(name: Name, decltpe: Option[Type] = None): Term.Param = {
    Term.Param(mods = Nil, name = name, decltpe = decltpe, default = None)
  }
}
