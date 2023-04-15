package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Pat

class PatSeqWildcardRendererImplTest extends UnitTestSuite {

  val patSeqWildcardRenderer = new PatSeqWildcardRendererImpl()

  test("render()") {
    patSeqWildcardRenderer.render(Pat.SeqWildcard())

    outputWriter.toString shouldBe "/* ... */"
  }

}
