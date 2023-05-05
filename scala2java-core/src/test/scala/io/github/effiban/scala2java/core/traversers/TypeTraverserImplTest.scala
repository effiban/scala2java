package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Type.Bounds
import scala.meta.{Mod, Type, XtensionQuasiquoteType}

class TypeTraverserImplTest extends UnitTestSuite {

  private val typeRefTraverser = mock[TypeRefTraverser]
  private val typeApplyTraverser = mock[TypeApplyTraverser]
  private val typeApplyInfixTraverser = mock[TypeApplyInfixTraverser]
  private val typeFunctionTraverser = mock[TypeFunctionTraverser]
  private val typeTupleTraverser = mock[TypeTupleTraverser]
  private val typeWithTraverser = mock[TypeWithTraverser]
  private val typeRefineTraverser = mock[TypeRefineTraverser]
  private val typeExistentialTraverser = mock[TypeExistentialTraverser]
  private val typeAnnotateTraverser = mock[TypeAnnotateTraverser]
  private val typeWildcardTraverser = mock[TypeWildcardTraverser]
  private val typeByNameTraverser = mock[TypeByNameTraverser]
  private val typeRepeatedTraverser = mock[TypeRepeatedTraverser]

  private val typeTraverser = new TypeTraverserImpl(
    typeRefTraverser,
    typeApplyTraverser,
    typeApplyInfixTraverser,
    typeFunctionTraverser,
    typeTupleTraverser,
    typeWithTraverser,
    typeRefineTraverser,
    typeExistentialTraverser,
    typeAnnotateTraverser,
    typeWildcardTraverser,
    typeByNameTraverser,
    typeRepeatedTraverser
  )

  test("traverse Type.Name") {
    val typeName = Type.Name("T")
    val traversedTypeName = Type.Name("U")
    doReturn(traversedTypeName).when(typeRefTraverser).traverse(eqTree(typeName))
    typeTraverser.traverse(typeName).structure shouldBe traversedTypeName.structure
  }

  test("traverse Type.Project") {
    val typeProject = t"A#B"
    val traversedTypeProject = t"C#D"
    doReturn(traversedTypeProject).when(typeRefTraverser).traverse(eqTree(typeProject))
    typeTraverser.traverse(typeProject).structure shouldBe traversedTypeProject.structure
  }

  test("traverse Type.Apply") {
    val typeApply = t"Map[K, V]"
    val traversedTypeApply = t"Map[U, W]"
    doReturn(traversedTypeApply).when(typeApplyTraverser).traverse(eqTree(typeApply))
    typeTraverser.traverse(typeApply).structure shouldBe traversedTypeApply.structure
  }

  test("traverse Type.ApplyInfix") {
    val typeApplyInfix = t"K Map V"
    doReturn(typeApplyInfix).when(typeApplyInfixTraverser).traverse(eqTree(typeApplyInfix))
    typeTraverser.traverse(typeApplyInfix).structure shouldBe typeApplyInfix.structure
  }

  test("traverse Type.Function") {
    val typeFunction = t"(Int, String) => Int"
    val expectedType = t"BiFunction[Int, String, Int]"
    doReturn(expectedType).when(typeFunctionTraverser).traverse(eqTree(typeFunction))
    typeTraverser.traverse(typeFunction).structure shouldBe expectedType.structure
  }

  test("traverse Type.Tuple") {
    val typeTuple = t"(Int, String)"
    val expectedTypeApply = t"Tuple2[Int, String]"
    doReturn(expectedTypeApply).when(typeTupleTraverser).traverse(eqTree(typeTuple))
    typeTraverser.traverse(typeTuple).structure shouldBe expectedTypeApply.structure
  }

  test("traverse Type.With") {
    val typeWith = t"MyType with Comparable"
    val traversedTypeWith = t"MyTraversedType with Comparable"
    doReturn(traversedTypeWith).when(typeWithTraverser).traverse(eqTree(typeWith))
    typeTraverser.traverse(typeWith).structure shouldBe traversedTypeWith.structure
  }

  test("traverse Type.Refine") {
    val typeRefine = t"MyType { val x: Int }"
    val traversedTypeRefine = t"MyTraversedType { val x: Int }"
    doReturn(traversedTypeRefine).when(typeRefineTraverser).traverse(eqTree(typeRefine))
    typeTraverser.traverse(typeRefine).structure shouldBe traversedTypeRefine.structure
  }

  test("traverse Type.Existential") {
    val typeExistential = t"MyType forSome { val x: Int}"
    val traversedTypeExistential = t"MyTraversedType forSome { val x: Int}"
    doReturn(traversedTypeExistential).when(typeExistentialTraverser).traverse(eqTree(typeExistential))
    typeTraverser.traverse(typeExistential).structure shouldBe traversedTypeExistential.structure
  }

  test("traverse Type.Annotate") {
    val typeAnnotate = t"MyType @MyAnnot1"
    val traversedTypeAnnotate = t"MyTraversedType @MyAnnot1"
    doReturn(traversedTypeAnnotate).when(typeAnnotateTraverser).traverse(eqTree(typeAnnotate))
    typeTraverser.traverse(typeAnnotate).structure shouldBe traversedTypeAnnotate.structure
  }

  test("traverse Type.Wildcard") {
    val typeWildcard = Type.Wildcard(Type.Bounds(lo = None, hi = Some(t"MyType")))
    val traversedTypeWildcard = Type.Wildcard(Type.Bounds(lo = None, hi = Some(t"MyTraversedType")))
    doReturn(traversedTypeWildcard).when(typeWildcardTraverser).traverse(eqTree(typeWildcard))
    typeTraverser.traverse(typeWildcard).structure shouldBe traversedTypeWildcard.structure
  }

  test("traverse Type.ByName") {
    val typeByName = t"=> Int"
    val expectedTypeApply = t"Supplier[Int]"
    doReturn(expectedTypeApply).when(typeByNameTraverser).traverse(eqTree(typeByName))
    typeTraverser.traverse(typeByName).structure shouldBe expectedTypeApply.structure
  }

  test("traverse Type.Repeated") {
    val typeRepeated = Type.Repeated(TypeNames.Double)
    val traversedTypeRepeated = Type.Repeated(t"double")
    doReturn(traversedTypeRepeated).when(typeRepeatedTraverser).traverse(eqTree(typeRepeated))
    typeTraverser.traverse(typeRepeated).structure shouldBe traversedTypeRepeated.structure
  }

  test("traverse Type.Var") {
    val typeVar = Type.Var(Type.Name("x"))
    typeTraverser.traverse(typeVar).structure shouldBe typeVar.structure
  }

  test("traverse Type.AnonymousParam") {
    val typeAnonymousParam = Type.AnonymousParam(Some(Mod.Contravariant()))
    typeTraverser.traverse(typeAnonymousParam).structure shouldBe typeAnonymousParam.structure
  }

  private def typeParamOf(name: String) = {
    Type.Param(
      mods = List(),
      name = Type.Name(name),
      tparams = List(),
      tbounds = Bounds(lo = None, hi = None),
      vbounds = List(),
      cbounds = List()
    )
  }
}
