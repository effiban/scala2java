package effiban.scala2java.orderings

import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{PrimaryCtors, TypeBounds, TypeNames}

import scala.meta.{Ctor, Decl, Defn, Init, Lit, Name, Pat, Term, Tree, Type}

class JavaTemplateChildOrderingTest extends UnitTestSuite {

  private val x = Term.Name("x")
  private val myMethod = Term.Name("myMethod")
  private val myType= Type.Name("MyType")

  private val defnVal = Defn.Val(
    mods = Nil,
    pats = List(Pat.Var(x)),
    decltpe = None,
    rhs = Lit.Int(2)
  )

  private val declVal = Decl.Val(
    mods = Nil,
    pats = List(Pat.Var(x)),
    decltpe = TypeNames.Int
  )

  private val ctorPrimary = PrimaryCtors.Empty

  private val init = Init(
      tpe = Type.Singleton(ref = Term.This(Name.Anonymous())),
      name = Name.Anonymous(),
      argss = List(List(Lit.String("param")))
    )

  private val ctorSecondary = Ctor.Secondary(
    mods = Nil,
    name = Name.Anonymous(),
    paramss = List(Nil),
    init = init,
    stats = Nil
  )

  private val defnDef = Defn.Def(
    mods = Nil,
    name = myMethod,
    tparams = Nil,
    paramss = List(Nil),
    decltpe = Some(TypeNames.Int),
    body = Term.Assign(lhs = x, rhs = Lit.Int(3))
  )

  private val defnType = Defn.Type(
    mods = List(),
    name = myType,
    tparams = List(),
    body = Type.Name("MyOtherType")
  )

  private val declDef = Decl.Def(
    mods = Nil,
    name = myMethod,
    tparams = Nil,
    paramss = List(Nil),
    decltpe = TypeNames.Int
  )

  private val declType = Decl.Type(
    mods = List(),
    name = myType,
    tparams = List(),
    bounds = TypeBounds.Empty
  )

  private val ChildTypeComparisons = Table(
    ("ChildType1", "ChildType2"),
    (defnVal, declVal),
    (declVal, ctorPrimary),
    (ctorPrimary, ctorSecondary),
    (ctorSecondary, defnDef),
    (defnDef, defnType),
    (defnType, declDef),
    (declDef, declType)
  )

  forAll(ChildTypeComparisons) { case (child1: Tree, child2: Tree) =>
    JavaTemplateChildOrdering.compare(child1, child2) should be < 0
  }
}
