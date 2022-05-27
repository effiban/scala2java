package com.effiban.scala2java

import com.effiban.scala2java.stubs._

import scala.meta.Pat.{Alternative, Bind}
import scala.meta.{Lit, Pat, Term, Type}

class PatTraverserImplTest extends UnitTestSuite {

  val patTraverser = new PatTraverserImpl(
    new StubTermNameTraverser,
    new StubPatWildcardTraverser,
    new StubPatSeqWildcardTraverser,
    new StubPatVarTraverser,
    new StubBindTraverser,
    new StubAlternativeTraverser,
    new StubPatTupleTraverser,
    new StubPatExtractTraverser,
    new StubPatExtractInfixTraverser,
    new StubPatInterpolateTraverser,
    new StubPatTypedTraverser)


  test("traverse Term.Name") {
    patTraverser.traverse(Term.Name("x"))
    outputWriter.toString shouldBe "x"
  }

  test("traverse Pat.Wildcard") {
    patTraverser.traverse(Pat.Wildcard())
    outputWriter.toString shouldBe "default"
  }

  test("traverse Pat.SeqWildcard") {
    patTraverser.traverse(Pat.SeqWildcard())
    outputWriter.toString shouldBe "/* ... */"
  }

  test("traverse Pat.Var") {
    patTraverser.traverse(Pat.Var(Term.Name("x")))
    outputWriter.toString shouldBe "x"
  }

  test("traverse Bind") {
    patTraverser.traverse(Bind(lhs = Pat.Var(Term.Name("x")), rhs = Term.Name("X")))
    outputWriter.toString shouldBe "/* x @ X */"
  }

  test("traverse Alternative") {
    patTraverser.traverse(Alternative(lhs = Lit.Int(2), rhs = Lit.Int(3)))
    outputWriter.toString shouldBe "2, 3"
  }

  test("traverse Pat.Tuple") {
    patTraverser.traverse(Pat.Tuple(List(Lit.String("myName"), Lit.Int(2), Lit.Boolean(true))))
    outputWriter.toString shouldBe """/* ("myName", 2, true) */"""
  }

  test("traverse Pat.Extract") {
    patTraverser.traverse(Pat.Extract(fun = Term.Name("MyRecord"), args = List(Pat.Var(Term.Name("x")), Lit.Int(3))))
    outputWriter.toString shouldBe "/* MyRecord(x, 3) */"
  }

  test("traverse Pat.ExtractInfix") {
    patTraverser.traverse(Pat.ExtractInfix(lhs = Pat.Var(Term.Name("x")), op = Term.Name("MyRecord"), rhs = List(Lit.Int(3))))
    outputWriter.toString shouldBe "/* MyRecord(x, 3) */"
  }

  test("traverse Pat.Interpolate") {
    val patInterpolate = Pat.Interpolate(
      prefix = Term.Name("r"),
      parts = List(Lit.String("Hello "), Lit.String(", have a (.+) day")),
      args = List(Term.Name("name"))
    )
    patTraverser.traverse(patInterpolate)
    outputWriter.toString shouldBe """/* r"Hello ${`name`}, have a (.+) day" */"""
  }

  test("traverse Pat.Typed") {
    val patTyped = Pat.Typed(lhs = Pat.Var(Term.Name("x")), rhs = Type.Name("MyType"))
    patTraverser.traverse(patTyped)
    outputWriter.toString shouldBe "MyType x"
  }
}
