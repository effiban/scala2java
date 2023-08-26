package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class TypeSelectRendererImplTest extends UnitTestSuite {

  private val defaultTermRefRenderer = mock[DefaultTermRefRenderer]
  private val typeNameRenderer = mock[TypeNameRenderer]
  private val arrayTypeRenderer = mock[ArrayTypeRenderer]

  private val typeSelectRenderer = new TypeSelectRendererImpl(
    defaultTermRefRenderer,
    typeNameRenderer,
    arrayTypeRenderer
  )

  test("render() for a non-Array") {
    val qual = q"myObj"
    val tpe = t"MyType"

    val typeSelect = t"myObj.MyType"

    doWrite("myObj").when(defaultTermRefRenderer).render(eqTree(qual))
    doWrite("MyType").when(typeNameRenderer).render(eqTree(tpe))

    typeSelectRenderer.render(typeSelect)

    outputWriter.toString shouldBe "myObj.MyType"
  }

  test("render() for an untyped Array") {
    val typeSelect = t"scala.Array"

    typeSelectRenderer.render(typeSelect)

    verify(arrayTypeRenderer).render(eqTree(t"Object"))
  }
}
