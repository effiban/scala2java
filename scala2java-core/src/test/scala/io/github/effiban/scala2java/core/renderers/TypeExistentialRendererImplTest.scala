package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Decl, Type}

class TypeExistentialRendererImplTest extends UnitTestSuite {

  private val typeRenderer = mock[TypeRenderer]

  private val typeExistentialRenderer = new TypeExistentialRendererImpl(typeRenderer)

  test("render") {
    val typeA = Type.Name("A")
    val typeB = Type.Name("B")

    val stats = List(Decl.Type(mods = Nil, name = typeB, tparams = Nil, bounds = Type.Bounds(None, None)))

    val typeExistential = Type.Existential(tpe = typeA, stats = stats)

    doWrite("A").when(typeRenderer).render(eqTree(typeA))

    typeExistentialRenderer.render(typeExistential)

    outputWriter.toString shouldBe "A/* forSome List(type B) */"
  }

}
