package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Type

class TypeRepeatedRendererImplTest extends UnitTestSuite {

  private val typeRenderer = mock[TypeRenderer]

  val typeRepeatedRenderer = new TypeRepeatedRendererImpl(typeRenderer)

  test("traverse()") {
    val repeatedType = Type.Repeated(TypeNames.String)

    doWrite("String").when(typeRenderer).render(eqTree(TypeNames.String))

    typeRepeatedRenderer.render(repeatedType)

    outputWriter.toString shouldBe "String..."
  }
}
