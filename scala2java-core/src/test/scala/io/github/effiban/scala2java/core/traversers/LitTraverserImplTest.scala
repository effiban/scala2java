package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Lit

class LitTraverserImplTest extends UnitTestSuite {

  private val litTraverser = new LitTraverserImpl()

  test("traverse Int") {
    litTraverser.traverse(Lit.Int(3))
    outputWriter.toString shouldBe "3"
  }

  test("traverse Short") {
    litTraverser.traverse(Lit.Short(3))
    outputWriter.toString shouldBe "3"
  }

  test("traverse Long") {
    litTraverser.traverse(Lit.Long(3))
    outputWriter.toString shouldBe "3"
  }

  test("traverse Float") {
    litTraverser.traverse(Lit.Float(3.4f))
    outputWriter.toString shouldBe "3.4"
  }

  test("traverse Double") {
    litTraverser.traverse(Lit.Double(3.4))
    outputWriter.toString shouldBe "3.4"
  }

  test("traverse Boolean") {
    litTraverser.traverse(Lit.Boolean(true))
    outputWriter.toString shouldBe "true"
  }

  test("traverse Char") {
    litTraverser.traverse(Lit.Char('a'))
    outputWriter.toString shouldBe "a"
  }

  test("traverse Unit") {
    litTraverser.traverse(Lit.Unit())
    outputWriter.toString shouldBe ""
  }

  test("traverse Null") {
    litTraverser.traverse(Lit.Null())
    outputWriter.toString shouldBe "null"
  }

  test("traverse Symbol") {
    litTraverser.traverse(Lit.Symbol(scala.Symbol("empty")))
    outputWriter.toString shouldBe """"empty""""
  }

  test("traverse String without special chars should quote the string") {
    litTraverser.traverse(Lit.String("abc"))
    outputWriter.toString shouldBe """"abc""""
  }

  test("traverse String with '\n' should leave the '\n' unchanged and quote the string") {
    litTraverser.traverse(Lit.String("abc\n"))
    outputWriter.toString shouldBe """"abc\n""""
  }
}
