package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaUnit
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Decl, Term, Type}

class TypeRefineRendererImplTest extends UnitTestSuite {

  private val Statements = List(
    Decl.Def(
      mods = Nil,
      name = Term.Name("fun"),
      tparams = Nil,
      paramss = List(List(Term.Param(mods = Nil, name = Term.Name("param"), decltpe = None, default = None))),
      decltpe = ScalaUnit
    )
  )

  private val typeRenderer = mock[TypeRenderer]

  private val typeRefineRenderer = new TypeRefineRendererImpl(typeRenderer)

  test("traverse when has type + stats") {
    val tpe = Type.Name("A")
    val refinedType = Type.Refine(tpe = Some(tpe), stats = Statements)

    doWrite("A").when(typeRenderer).render(eqTree(tpe))

    typeRefineRenderer.render(refinedType)

    outputWriter.toString shouldBe "A/* List(def fun(param): scala.Unit) */"
  }

  test("traverse when has stats only") {
    val refinedType = Type.Refine(tpe = None, stats = Statements)

    typeRefineRenderer.render(refinedType)

    outputWriter.toString shouldBe "/* List(def fun(param): scala.Unit) */"
  }
}
