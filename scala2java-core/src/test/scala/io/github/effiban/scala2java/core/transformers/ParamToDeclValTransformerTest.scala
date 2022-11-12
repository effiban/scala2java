package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TraversalConstants.UnknownType
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames

import scala.meta.Mod.{Final, Private}
import scala.meta.{Decl, Name, Pat, Term, Type}

class ParamToDeclValTransformerTest extends UnitTestSuite {

  test("transform when no declared type") {
    val paramName = Term.Name("x")

    val param = Term.Param(
      mods = Nil,
      name = paramName,
      decltpe = None,
      default = None
    )

    val expectedDeclVal = Decl.Val(
      mods = List(Private(within = Name.Anonymous()), Final()),
      pats = List(Pat.Var(paramName)),
      decltpe = Type.Name(UnknownType)
    )

    val actualDeclVal = ParamToDeclValTransformer.transform(param)

    actualDeclVal.structure shouldBe expectedDeclVal.structure
  }

  test("transform when has declared type") {
    val paramName = Term.Name("x")

    val param = Term.Param(
      mods = Nil,
      name = paramName,
      decltpe = Some(TypeNames.Int),
      default = None
    )

    val expectedDeclVal = Decl.Val(
      mods = List(Private(within = Name.Anonymous()), Final()),
      pats = List(Pat.Var(paramName)),
      decltpe = TypeNames.Int
    )

    val actualDeclVal = ParamToDeclValTransformer.transform(param)

    actualDeclVal.structure shouldBe expectedDeclVal.structure
  }
}