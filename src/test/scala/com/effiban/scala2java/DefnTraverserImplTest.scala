package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeMatcher.eqTree
import com.effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers

import scala.meta.Ctor.Primary
import scala.meta.Term.Apply
import scala.meta.{Decl, Defn, Lit, Name, Pat, Self, Template, Term, Type}

class DefnTraverserImplTest extends UnitTestSuite {

  private val defnValTraverser  = mock[DefnValTraverser]
  private val defnVarTraverser  = mock[DefnVarTraverser]
  private val defnDefTraverser  = mock[DefnDefTraverser]
  private val defnTypeTraverser = mock[DefnTypeTraverser]
  private val classTraverser    = mock[ClassTraverser]
  private val traitTraverser    = mock[TraitTraverser]
  private val objectTraverser   = mock[ObjectTraverser]

  private val defnTraverser = new DefnTraverserImpl(
    defnValTraverser,
    defnVarTraverser,
    defnDefTraverser,
    defnTypeTraverser,
    classTraverser,
    traitTraverser,
    objectTraverser)

  test("traverse() for Defn.Val") {

    val defnVal = Defn.Val(
      mods = List(),
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = Some(TypeNames.Int),
      rhs = Lit.Int(3)
    )

    defnTraverser.traverse(defnVal)

    verify(defnValTraverser).traverse(eqTree(defnVal))
  }

  test("traverse() for Defn.Var") {

    val defnVar = Defn.Var(
      mods = List(),
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = Some(TypeNames.Int),
      rhs = Some(Lit.Int(3))
    )

    defnTraverser.traverse(defnVar)

    verify(defnVarTraverser).traverse(eqTree(defnVar))
  }

  test("traverse() for Defn.Def") {

    val defnDef = Defn.Def(
      mods = List(),
      name = Term.Name("myMethod"),
      tparams = List(),
      paramss = List(),
      decltpe = Some(TypeNames.Int),
      body = Term.Apply(Term.Name("doSomething"), List())
    )

    defnTraverser.traverse(defnDef)

    verify(defnDefTraverser).traverse(defnDef = eqTree(defnDef), maybeInit = ArgumentMatchers.eq(None))
  }

  test("traverse() for Defn.Type") {

    val defnType = Defn.Type(
      mods = List(),
      name = Type.Name("MyType"),
      tparams = List(),
      body = Type.Name("MyOtherType")
    )

    defnTraverser.traverse(defnType)

    verify(defnTypeTraverser).traverse(eqTree(defnType))
  }

  test("traverse() for Defn.Class") {

    val defnClass = Defn.Class(
      mods = List(),
      name = Type.Name("MyClass"),
      tparams = List(),
      ctor = Primary(
        mods = List(),
        name = Name.Anonymous(),
        paramss = List(List(termParam("param1", "Int"), termParam("param2", "String")))
      ),
      templ = Template(
        early = List(),
        inits = List(),
        self = Self(Name.Anonymous(), None),
        stats = List(
          Apply(Term.Name("doSomething"), List(Lit.String("input")))
        )
      )
    )

    defnTraverser.traverse(defnClass)

    verify(classTraverser).traverse(eqTree(defnClass))
  }

  test("traverse() for Trait") {

    val defnTrait = Defn.Trait(
      mods = List(),
      name = Type.Name("MyTrait"),
      tparams = List(),
      ctor = Primary(
        mods = List(),
        name = Name.Anonymous(),
        paramss = List()
      ),
      templ = Template(
        early = List(),
        inits = List(),
        self = Self(Name.Anonymous(), None),
        stats = List(
          Decl.Val(
            mods = List(),
            pats = List(Pat.Var(Term.Name("x"))),
            decltpe = TypeNames.Int
          )
        )
      )
    )

    defnTraverser.traverse(defnTrait)

    verify(traitTraverser).traverse(eqTree(defnTrait))
  }

  test("traverse() for Object") {

    val defnObject = Defn.Object(
      mods = List(),
      name = Term.Name("MyObject"),
      templ = Template(
        early = List(),
        inits = List(),
        self = Self(Name.Anonymous(), None),
        stats = List(
          Defn.Val(
            mods = List(),
            pats = List(Pat.Var(Term.Name("x"))),
            decltpe = Some(TypeNames.Int),
            rhs = Lit.Int(3)
          )
        )
      )
    )

    defnTraverser.traverse(defnObject)
  }

  private def termParam(name: String, typeName: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(Type.Name(typeName)), default = None)
  }
}
