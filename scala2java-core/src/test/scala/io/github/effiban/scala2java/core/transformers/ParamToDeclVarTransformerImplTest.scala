package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.entities.TraversalConstants.UnknownType
import io.github.effiban.scala2java.core.entities.TypeSelects
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Mod.{Final, Private}
import scala.meta.{Decl, Name, Pat, Term, Type}

class ParamToDeclVarTransformerImplTest extends UnitTestSuite {

  private val typeByNameToSupplierTypeTransformer = mock[TypeByNameToSupplierTypeTransformer]

  private val paramToDeclVarTransformer = new ParamToDeclVarTransformerImpl(typeByNameToSupplierTypeTransformer)

  test("transform when no declared type") {
    val paramName = Term.Name("x")

    val param = Term.Param(
      mods = Nil,
      name = paramName,
      decltpe = None,
      default = None
    )

    val expectedDeclVar = Decl.Var(
      mods = List(Private(within = Name.Anonymous()), Final()),
      pats = List(Pat.Var(paramName)),
      decltpe = Type.Name(UnknownType)
    )

    val actualDeclVar = paramToDeclVarTransformer.transform(param)

    actualDeclVar.structure shouldBe expectedDeclVar.structure
  }

  test("transform when has declared regular type") {
    val paramName = Term.Name("x")

    val param = Term.Param(
      mods = Nil,
      name = paramName,
      decltpe = Some(TypeNames.Int),
      default = None
    )

    val expectedDeclVar = Decl.Var(
      mods = List(Private(within = Name.Anonymous()), Final()),
      pats = List(Pat.Var(paramName)),
      decltpe = TypeNames.Int
    )

    val actualDeclVal = paramToDeclVarTransformer.transform(param)

    actualDeclVal.structure shouldBe expectedDeclVar.structure
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

    val expectedSupplierType = Type.Apply(TypeSelects.JavaSupplier, List(TypeNames.Int))

    val expectedDeclVar = Decl.Var(
      mods = List(Private(within = Name.Anonymous()), Final()),
      pats = List(Pat.Var(paramName)),
      decltpe = expectedSupplierType
    )

    when(typeByNameToSupplierTypeTransformer.transform(eqTree(typeByName))).thenReturn(expectedSupplierType)

    val actualDeclVar = paramToDeclVarTransformer.transform(param)

    actualDeclVar.structure shouldBe expectedDeclVar.structure
  }
}
