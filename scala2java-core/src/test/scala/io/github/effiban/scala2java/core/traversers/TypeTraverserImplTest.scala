package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TypeBounds, TypeNames}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Mod.Annot
import scala.meta.Type.Bounds
import scala.meta.{Decl, Init, Mod, Name, Pat, Term, Type, XtensionQuasiquoteType}

class TypeTraverserImplTest extends UnitTestSuite {

  private val typeRefTraverser = mock[TypeRefTraverser]
  private val typeProjectTraverser = mock[TypeProjectTraverser]
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
  private val typeRenderer = mock[TypeRenderer]

  private val typeTraverser = new TypeTraverserImpl(
    typeRefTraverser,
    typeProjectTraverser,
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
    typeRepeatedTraverser,
    typeRenderer
  )

  test("traverse Type.Name") {
    val typeName = Type.Name("T")
    val traversedTypeName = Type.Name("U")

    doReturn(traversedTypeName).when(typeRefTraverser).traverse(eqTree(typeName))

    typeTraverser.traverse(typeName)

    verify(typeRenderer).render(eqTree(traversedTypeName))
  }

  test("traverse Type.Project") {
    val typeProject = t"A#B"
    typeTraverser.traverse(typeProject)
    verify(typeProjectTraverser).traverse(eqTree(typeProject))
  }

  test("traverse Type.Apply") {
    val typeApply = Type.Apply(tpe = Type.Name("Map"), args = List(Type.Name("K"), Type.Name("V")))
    typeTraverser.traverse(typeApply)
    verify(typeApplyTraverser).traverse(eqTree(typeApply))
  }

  test("traverse Type.ApplyInfix") {
    val typeApplyInfix = t"K Map V"
    val traversedTypeApplyInfix = t"U Map W"

    doReturn(traversedTypeApplyInfix).when(typeApplyInfixTraverser).traverse(eqTree(typeApplyInfix))

    typeTraverser.traverse(typeApplyInfix)

    verify(typeRenderer).render(eqTree(traversedTypeApplyInfix))
  }

  test("traverse Type.Function") {
    val typeFunction = Type.Function(params = List(TypeNames.Int, TypeNames.String), res = TypeNames.String)
    typeTraverser.traverse(typeFunction)
    verify(typeFunctionTraverser).traverse(eqTree(typeFunction))
  }

  test("traverse Type.Tuple") {
    val typeTuple = Type.Tuple(List(TypeNames.Int, TypeNames.String))
    typeTraverser.traverse(typeTuple)
    verify(typeTupleTraverser).traverse(eqTree(typeTuple))
  }

  test("traverse Type.With") {
    val typeWith = Type.With(Type.Name("MyType"), Type.Name("Comparable"))
    typeTraverser.traverse(typeWith)
    verify(typeWithTraverser).traverse(eqTree(typeWith))
  }

  test("traverse Type.Refine") {
    val typeRefine = Type.Refine(
      tpe = Some(Type.Name("MyType")),
      stats = List(
        Decl.Val(
          mods = Nil,
          pats = List(Pat.Var(Term.Name("X"))),
          decltpe = TypeNames.Int
        )
      )
    )
    typeTraverser.traverse(typeRefine)
    verify(typeRefineTraverser).traverse(eqTree(typeRefine))
  }

  test("traverse Type.Existential") {
    val typeExistential = Type.Existential(
      tpe = Type.Name("MyType"),
      stats = List(
        Decl.Val(
          mods = Nil,
          pats = List(Pat.Var(Term.Name("X"))),
          decltpe = TypeNames.Int
        )
      )
    )
    typeTraverser.traverse(typeExistential)
    verify(typeExistentialTraverser).traverse(eqTree(typeExistential))
  }

  test("traverse Type.Annotate") {
    val typeAnnotate = Type.Annotate(
      tpe = Type.Name("MyType"),
      annots = List(Annot(Init(tpe = Type.Name("MyAnnot1"), name = Name.Anonymous(), argss = List())))
    )
    typeTraverser.traverse(typeAnnotate)
    verify(typeAnnotateTraverser).traverse(eqTree(typeAnnotate))
  }

  test("traverse Type.AnonymousParam") {
    val typeAnonymousParam = Type.AnonymousParam(Some(Mod.Contravariant()))
    typeTraverser.traverse(typeAnonymousParam)
    verify(typeRenderer).render(eqTree(typeAnonymousParam))
  }

  test("traverse Type.Wildcard") {
    val typeWildcard = Type.Wildcard(TypeBounds.Empty)
    typeTraverser.traverse(typeWildcard)
    verify(typeWildcardTraverser).traverse(eqTree(typeWildcard))
  }

  test("traverse Type.ByName") {
    val typeByName = Type.ByName(TypeNames.String)
    typeTraverser.traverse(typeByName)
    verify(typeByNameTraverser).traverse(eqTree(typeByName))
  }

  test("traverse Type.Repeated") {
    val typeRepeated = Type.Repeated(TypeNames.String)
    typeTraverser.traverse(typeRepeated)
    verify(typeRepeatedTraverser).traverse(eqTree(typeRepeated))
  }

  test("traverse Type.Var") {
    val typeVar = Type.Var(Type.Name("x"))
    typeTraverser.traverse(typeVar)
    verify(typeRenderer).render(eqTree(typeVar))
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
