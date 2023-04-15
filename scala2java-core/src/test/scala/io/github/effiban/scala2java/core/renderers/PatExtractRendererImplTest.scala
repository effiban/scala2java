package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Pat, Term}

class PatExtractRendererImplTest extends UnitTestSuite {

  val patExtractRenderer = new PatExtractRendererImpl()

  test("traverse") {
    val patExtract = Pat.Extract(
      fun = Term.Name("MyClass"),
      args = List(Pat.Var(Term.Name("x")), Pat.Var(Term.Name("y")))
    )
    patExtractRenderer.render(patExtract)

    outputWriter.toString shouldBe "/* MyClass(x, y) */"
  }

}
