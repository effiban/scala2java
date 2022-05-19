package com.effiban.scala2java

import com.effiban.scala2java.stubs._

import scala.meta.Ctor.Primary
import scala.meta.Term.Apply
import scala.meta.{Decl, Defn, Lit, Name, Pat, Self, Template, Term, Type}

class DefnTraverserImplTest extends UnitTestSuite {

  private val defnTraverser = new DefnTraverserImpl(
    new StubDefnValTraverser,
    new StubDefnVarTraverser,
    new StubDefnDefTraverser,
    new StubDefnTypeTraverser,
    new StubClassTraverser,
    new StubTraitTraverser,
    new StubObjectTraverser)

  test("traverse() for Defn.Val") {

    val defnVal = Defn.Val(
      mods = List(),
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = Some(Type.Name("Int")),
      rhs = Lit.Int(3)
    )

    defnTraverser.traverse(defnVal)

    outputWriter.toString shouldBe "int myVal = 3"
  }

  test("traverse() for Defn.Var when has RHS") {

    val defnVar = Defn.Var(
      mods = List(),
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = Some(Type.Name("Int")),
      rhs = Some(Lit.Int(3))
    )

    defnTraverser.traverse(defnVar)

    outputWriter.toString shouldBe "int myVar = 3"
  }

  test("traverse() for Defn.Var when has no RHS") {

    val defnVar = Defn.Var(
      mods = List(),
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = Some(Type.Name("Int")),
      rhs = None
    )

    defnTraverser.traverse(defnVar)

    outputWriter.toString shouldBe "int myVar"
  }

  test("traverse() for Defn.Def") {

    val defnDef = Defn.Def(
      mods = List(),
      name = Term.Name("myMethod"),
      tparams = List(),
      paramss = List(),
      decltpe = Some(Type.Name("Int")),
      body = Term.Apply(Term.Name("doSomething"), List())
    )

    defnTraverser.traverse(defnDef)

    outputWriter.toString shouldBe
      """int myMethod() {
        |doSomething()
        |}
        |""".stripMargin
  }

  test("traverse() for Defn.Type") {

    val defnType = Defn.Type(
      mods = List(),
      name = Type.Name("MyType"),
      tparams = List(),
      body = Type.Name("MyOtherType")
    )

    defnTraverser.traverse(defnType)

    outputWriter.toString shouldBe
      """interface MyType {
        |/* MyOtherType */
        |}
        |""".stripMargin
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

    outputWriter.toString shouldBe
      """/**
        |* STUB CLASS - Scala code:
        |* class MyClass(param1: Int, param2: String) { doSomething("input") }
        |*/
        |""".stripMargin
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
            decltpe = Type.Name("Int")
          )
        )
      )
    )

    defnTraverser.traverse(defnTrait)

    outputWriter.toString shouldBe
      """/**
        |* STUB TRAIT - Scala code:
        |* trait MyTrait { val x: Int }
        |*/
        |""".stripMargin
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
            decltpe = Some(Type.Name("Int")),
            rhs = Lit.Int(3)
          )
        )
      )
    )

    defnTraverser.traverse(defnObject)

    outputWriter.toString shouldBe
      """/**
        |* STUB OBJECT - Scala code:
        |* object MyObject { val x: Int = 3 }
        |*/
        |""".stripMargin
  }

  private def termParam(name: String, typeName: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(Type.Name(typeName)), default = None)
  }
}
