package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.VarRenderContext
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteType

class DefnVarTypeRendererImplTest extends UnitTestSuite {

  private val typeRenderer = mock[TypeRenderer]

  private val defnVarTypeRenderer = new DefnVarTypeRendererImpl(typeRenderer)

  test("render when has declared type should render it") {
    doWrite("int").when(typeRenderer).render(eqTree(t"int"))

    defnVarTypeRenderer.render(
      maybeDeclType = Some(t"int"),
      context = VarRenderContext()
    )

    outputWriter.toString shouldBe "int"
  }

  test("render when has no declared type and not in block - should write 'UnknownType'") {
    defnVarTypeRenderer.render(
      maybeDeclType = None,
      context = VarRenderContext()
    )

    outputWriter.toString shouldBe "/* UnknownType */"
  }

  test("render when has no declared type and in Block - should write 'var'") {
    defnVarTypeRenderer.render(
      maybeDeclType = None,
      context = VarRenderContext(inBlock = true)
    )

    outputWriter.toString shouldBe "var"
  }
}
