package com.effiban.scala2java

import com.effiban.scala2java.stubs.StubTermTraverser

import scala.meta.Enumerator.{CaseGenerator, Generator}
import scala.meta.{Enumerator, Pat, Term}

class ForVariantTraverserImplTest extends UnitTestSuite {

  private val MapFunctionName = Term.Name("map")

  private val forVariantTraverser = new ForVariantTraverserImpl(new StubTermTraverser())

  test("traverse() for one Generator with a variable in the LHS") {
    val x = Term.Name("x")

    val enums = List(
      Generator(pat = Pat.Var(x), rhs = Term.Name("xs")),
    )
    val body = Term.Apply(Term.Name("result"), List(x))

    forVariantTraverser.traverse(enums, body, MapFunctionName)

    outputWriter.toString shouldBe "xs.map(x => result(x))"
  }

  test("traverse() for one Generator enumerator with a wildcard in the LHS") {
    val enums = List(
      Generator(pat = Pat.Wildcard(), rhs = Term.Name("xs")),
    )
    val body = Term.Apply(Term.Name("result"), Nil)

    forVariantTraverser.traverse(enums, body, MapFunctionName)

    outputWriter.toString shouldBe "xs.map(__ => result())"
  }

  test("traverse() for one CaseGenerator enumerator with a variable in the LHS") {
    val x = Term.Name("x")

    val enums = List(
      CaseGenerator(pat = Pat.Var(x), rhs = Term.Name("xs")),
    )
    val body = Term.Apply(Term.Name("result"), List(x))

    forVariantTraverser.traverse(enums, body, MapFunctionName)

    outputWriter.toString shouldBe "xs.map(x => result(x))"
  }

  test("traverse() for one Val enumerator with a variable in the LHS") {
    val x = Term.Name("x")

    val enums = List(
      Enumerator.Val(pat = Pat.Var(x), rhs = Term.Name("xs")),
    )
    val body = Term.Apply(Term.Name("result"), List(x))

    forVariantTraverser.traverse(enums, body, MapFunctionName)

    outputWriter.toString shouldBe "xs.map(x => result(x))"
  }

  test("traverse() for three enumerators") {
    val x = Term.Name("x")
    val y = Term.Name("y")
    val z = Term.Name("z")

    val enums = List(
        Generator(pat = Pat.Var(x), rhs = Term.Name("xs")),
        Generator(pat = Pat.Var(y), rhs = Term.Name("ys")),
        Generator(pat = Pat.Var(z), rhs = Term.Name("zs"))
      )
    val body = Term.Apply(Term.Name("result"), List(x, y, z))

    forVariantTraverser.traverse(enums, body, MapFunctionName)

    outputWriter.toString shouldBe "xs.flatMap(x => ys.flatMap(y => zs.map(z => result(x, y, z))))"
  }

}
