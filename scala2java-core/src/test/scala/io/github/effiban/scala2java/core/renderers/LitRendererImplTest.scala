package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Lit

class LitRendererImplTest extends UnitTestSuite {

  private val litRenderer = new LitRendererImpl()

  test("render Int") {
    litRenderer.render(Lit.Int(3))
    outputWriter.toString shouldBe "3"
  }

  test("render Short") {
    litRenderer.render(Lit.Short(3))
    outputWriter.toString shouldBe "3"
  }

  test("render Long") {
    litRenderer.render(Lit.Long(3))
    outputWriter.toString shouldBe "3"
  }

  test("render Float") {
    litRenderer.render(Lit.Float(3.4f))
    outputWriter.toString shouldBe "3.4"
  }

  test("render Double") {
    litRenderer.render(Lit.Double(3.4))
    outputWriter.toString shouldBe "3.4"
  }

  test("render Boolean") {
    litRenderer.render(Lit.Boolean(true))
    outputWriter.toString shouldBe "true"
  }

  test("render Char") {
    litRenderer.render(Lit.Char('a'))
    outputWriter.toString shouldBe "a"
  }

  test("render Unit") {
    litRenderer.render(Lit.Unit())
    outputWriter.toString shouldBe ""
  }

  test("render Null") {
    litRenderer.render(Lit.Null())
    outputWriter.toString shouldBe "null"
  }

  test("render Symbol") {
    litRenderer.render(Lit.Symbol(scala.Symbol("empty")))
    outputWriter.toString shouldBe """"empty""""
  }

  test("render String without special chars should quote the string") {
    litRenderer.render(Lit.String("abc"))
    outputWriter.toString shouldBe """"abc""""
  }

  test("render String with '\n' should leave the '\n' unchanged and quote the string") {
    litRenderer.render(Lit.String("abc\n"))
    outputWriter.toString shouldBe """"abc\n""""
  }
}
