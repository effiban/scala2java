package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteType

class ArrayTypeRendererImplTest extends UnitTestSuite {

  private val typeRenderer = mock[TypeRenderer]

  private val arrayTypeRenderer = new ArrayTypeRendererImpl(typeRenderer)

  test("render()") {
    val tpe = t"MyType"

    doWrite("MyType").when(typeRenderer).render(eqTree(tpe))

    arrayTypeRenderer.render(tpe)

    outputWriter.toString shouldBe "MyType[]"
  }

}
