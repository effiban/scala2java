package io.github.effiban.scala2java.core.orderings

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{PrimaryCtors, TypeBounds, TypeNames}

import scala.meta.{Ctor, Decl, Defn, Import, Importee, Importer, Init, Lit, Name, Pat, Term, Tree, Type}

class JavaTemplateChildOrderingTest extends UnitTestSuite {

  private val x = Term.Name("x")
  private val myMethod = Term.Name("myMethod")
  private val myType = Type.Name("MyType")

  private val `import` = Import(List(Importer(Term.Name("mypackage1"), List(Importee.Name(Name.Indeterminate("myclass1"))))))

  private val defnVal = Defn.Val(
    mods = Nil,
    pats = List(Pat.Var(x)),
    decltpe = None,
    rhs = Lit.Int(2)
  )

  private val defnVar = Defn.Var(
    mods = Nil,
    pats = List(Pat.Var(x)),
    decltpe = None,
    rhs = Some(Lit.Int(2))
  )

  private val declVal = Decl.Val(
    mods = Nil,
    pats = List(Pat.Var(x)),
    decltpe = TypeNames.Int
  )

  private val declVar = Decl.Var(
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
    (`import`, defnVal),
    (defnVal, defnVar),
    (defnVar, declVal),
    (declVal, declVar),
    (declVar, ctorPrimary),
    (ctorPrimary, ctorSecondary),
    (ctorSecondary, defnDef),
    (defnDef, defnType),
    (defnType, declDef),
    (declDef, declType)
  )

  forAll(ChildTypeComparisons) { case (child1: Tree, child2: Tree) =>
    test(s"${child1.getClass.getSimpleName} should precede ${child2.getClass.getSimpleName}") {
      JavaTemplateChildOrdering.compare(child1, child2) should be < 0
    }
  }
}
