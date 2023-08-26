package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.TypeSelects
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Type

class TypeApplyRendererImplTest extends UnitTestSuite {

  private val typeRenderer = mock[TypeRenderer]
  private val typeListRenderer = mock[TypeListRenderer]
  private val arrayTypeRenderer = mock[ArrayTypeRenderer]

  private val typeApplyRenderer = new TypeApplyRendererImpl(
    typeRenderer,
    typeListRenderer,
    arrayTypeRenderer
  )

  test("render() a 'Map'") {
    val tpe = TypeNames.Map
    val args = List(TypeNames.Int, TypeNames.String)

    val typeApply = Type.Apply(tpe = tpe, args = args)

    doWrite("Map").when(typeRenderer).render(eqTree(tpe))
    doWrite("<Integer, String>").when(typeListRenderer).render(eqTreeList(args))

    typeApplyRenderer.render(typeApply)

    outputWriter.toString shouldBe "Map<Integer, String>"
  }

  test("render() a valid 'Array'") {
    val tpe = TypeSelects.ScalaArray
    val args = List(TypeNames.String)

    val typeApply = Type.Apply(tpe = tpe, args = args)

    doWrite("String[]").when(arrayTypeRenderer).render(eqTree(TypeNames.String))

    typeApplyRenderer.render(typeApply)

    outputWriter.toString shouldBe "String[]"
  }

  test("render() an 'Array' with 2 type args should throw an exception") {
    val tpe = TypeSelects.ScalaArray
    val args = List(TypeNames.String, TypeNames.Int)

    val typeApply = Type.Apply(tpe = tpe, args = args)

    intercept[IllegalStateException] {
      typeApplyRenderer.render(typeApply)
    }
  }
}
