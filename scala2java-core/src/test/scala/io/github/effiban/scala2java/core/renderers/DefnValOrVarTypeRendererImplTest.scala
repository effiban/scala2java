package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ValOrVarRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteType

class DefnValOrVarTypeRendererImplTest extends UnitTestSuite {

  private val typeRenderer = mock[TypeRenderer]

  private val defnValOrVarTypeRenderer = new DefnValOrVarTypeRendererImpl(typeRenderer)

  test("render when has declared type should render it") {
    doWrite("int").when(typeRenderer).render(eqTree(t"int"))

    defnValOrVarTypeRenderer.render(
      maybeDeclType = Some(t"int"),
      context = ValOrVarRenderContext()
    )

    outputWriter.toString shouldBe "int"
  }

  test("render when has no declared type and not in block - should write 'UnknownType'") {
    defnValOrVarTypeRenderer.render(
      maybeDeclType = None,
      context = ValOrVarRenderContext()
    )

    outputWriter.toString shouldBe "/* UnknownType */"
  }

  test("render when has no declared type and in Block - should write 'var'") {
    defnValOrVarTypeRenderer.render(
      maybeDeclType = None,
      context = ValOrVarRenderContext(inBlock = true)
    )

    outputWriter.toString shouldBe "var"
  }
}
