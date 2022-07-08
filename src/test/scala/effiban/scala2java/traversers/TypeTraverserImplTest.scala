package effiban.scala2java.traversers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{TypeBounds, TypeNames}

import scala.meta.Mod.Annot
import scala.meta.Type.Bounds
import scala.meta.{Decl, Init, Name, Pat, Term, Type}

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
  private val typeLambdaTraverser = mock[TypeLambdaTraverser]
  private val typePlaceholderTraverser = mock[TypePlaceholderTraverser]
  private val typeByNameTraverser = mock[TypeByNameTraverser]
  private val typeRepeatedTraverser = mock[TypeRepeatedTraverser]
  private val typeVarTraverser = mock[TypeVarTraverser]

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
    typeLambdaTraverser,
    typePlaceholderTraverser,
    typeByNameTraverser,
    typeRepeatedTraverser,
    typeVarTraverser
  )

  test("traverse Type.Name") {
    val typeName = Type.Name("T")
    typeTraverser.traverse(typeName)
    verify(typeRefTraverser).traverse(eqTree(typeName))
  }

  test("traverse Type.Apply") {
    val typeApply = Type.Apply(tpe = Type.Name("Map"), args = List(Type.Name("K"), Type.Name("V")))
    typeTraverser.traverse(typeApply)
    verify(typeApplyTraverser).traverse(eqTree(typeApply))
  }

  test("traverse Type.ApplyInfix") {
    val typeApplyInfix = Type.ApplyInfix(lhs = Type.Name("K"), op = Type.Name("Map"), rhs = Type.Name("V"))
    typeTraverser.traverse(typeApplyInfix)
    verify(typeApplyInfixTraverser).traverse(eqTree(typeApplyInfix))
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

  test("traverse Type.Lambda") {
    val typeLambda = Type.Lambda(
      tparams = List(typeParamOf("T1"), typeParamOf("T2")),
      tpe = Type.Name("U")
    )
    typeTraverser.traverse(typeLambda)
    verify(typeLambdaTraverser).traverse(eqTree(typeLambda))
  }

  test("traverse Type.Placeholder") {
    val typePlaceholder = Type.Placeholder(TypeBounds.Empty)
    typeTraverser.traverse(typePlaceholder)
    verify(typePlaceholderTraverser).traverse(eqTree(typePlaceholder))
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
    verify(typeVarTraverser).traverse(eqTree(typeVar))
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
