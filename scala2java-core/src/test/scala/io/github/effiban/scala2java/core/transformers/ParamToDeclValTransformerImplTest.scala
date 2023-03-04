package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TraversalConstants.UnknownType
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Mod.{Final, Private}
import scala.meta.{Decl, Name, Pat, Term, Type}

class ParamToDeclValTransformerImplTest extends UnitTestSuite {

  private val typeByNameToSupplierTypeTransformer = mock[TypeByNameToSupplierTypeTransformer]

  private val paramToDeclValTransformer = new ParamToDeclValTransformerImpl(typeByNameToSupplierTypeTransformer)

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

    val actualDeclVal = paramToDeclValTransformer.transform(param)

    actualDeclVal.structure shouldBe expectedDeclVal.structure
  }

  test("transform when has declared regular type") {
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

    val actualDeclVal = paramToDeclValTransformer.transform(param)

    actualDeclVal.structure shouldBe expectedDeclVal.structure
  }

  test("transform when has declared type by-name") {
    val paramName = Term.Name("x")
    val typeByName = Type.ByName(TypeNames.Int)

    val param = Term.Param(
      mods = Nil,
      name = paramName,
      decltpe = Some(typeByName),
      default = None
    )

    val expectedSupplierType = Type.Apply(TypeNames.JavaSupplier, List(TypeNames.Int))

    val expectedDeclVal = Decl.Val(
      mods = List(Private(within = Name.Anonymous()), Final()),
      pats = List(Pat.Var(paramName)),
      decltpe = expectedSupplierType
    )

    when(typeByNameToSupplierTypeTransformer.transform(eqTree(typeByName))).thenReturn(expectedSupplierType)

    val actualDeclVal = paramToDeclValTransformer.transform(param)

    actualDeclVal.structure shouldBe expectedDeclVal.structure
  }
}
