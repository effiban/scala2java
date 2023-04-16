package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.TraversalConstants.JavaPlaceholder
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Term

class TermPlaceholderRendererImplTest extends UnitTestSuite {

  val termPlaceholderRenderer = new TermPlaceholderRendererImpl()

  test("render()") {
    termPlaceholderRenderer.render(Term.Placeholder())

    outputWriter.toString shouldBe JavaPlaceholder
  }

}
