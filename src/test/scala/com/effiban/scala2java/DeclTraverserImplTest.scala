package com.effiban.scala2java

import com.effiban.scala2java.stubs.{StubDeclDefTraverser, StubDeclTypeTraverser, StubDeclValTraverser, StubDeclVarTraverser}

import scala.meta.Type.Bounds
import scala.meta.{Decl, Pat, Term, Type}

class DeclTraverserImplTest extends UnitTestSuite {

  private val declTraverser = new DeclTraverserImpl(
    new StubDeclValTraverser,
    new StubDeclVarTraverser,
    new StubDeclDefTraverser,
    new StubDeclTypeTraverser)

  test("traverse() a Decl.Val") {

    val declVal = Decl.Val(
      mods = List(),
      pats = List(Pat.Var(Term.Name("myVal"))),
      decltpe = Type.Name("Int")
    )

    declTraverser.traverse(declVal)

    outputWriter.toString shouldBe "int myVal"
  }

  test("traverse() a Decl.Var") {

    val declVar = Decl.Var(
      mods = List(),
      pats = List(Pat.Var(Term.Name("myVar"))),
      decltpe = Type.Name("Int")
    )

    declTraverser.traverse(declVar)

    outputWriter.toString shouldBe "int myVar"
  }

  test("traverse() a Decl.Def") {

    val declDef = Decl.Def(
      mods = List(),
      name = Term.Name("myMethod"),
      tparams = List(),
      paramss = List(),
      decltpe = Type.Name("Int")
    )

    declTraverser.traverse(declDef)

    outputWriter.toString shouldBe "int myMethod()"
  }

  test("traverse() a Decl.Type") {

    val declType = Decl.Type(
      mods = List(),
      name = Type.Name("MyType"),
      tparams = List(),
      bounds = Bounds(lo = None, hi = None)
    )

    declTraverser.traverse(declType)

    outputWriter.toString shouldBe
      """interface MyType {
        |}
        |""".stripMargin
  }
}
