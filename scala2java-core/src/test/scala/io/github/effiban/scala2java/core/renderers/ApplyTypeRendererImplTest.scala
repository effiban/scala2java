package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class ApplyTypeRendererImplTest extends UnitTestSuite {

  private val classOfRenderer = mock[ClassOfRenderer]

  private val applyTypeRenderer = new ApplyTypeRendererImpl(classOfRenderer)

  test("render() for classOf[..]") {
    val classOfType = q"classOf[T]"

    applyTypeRenderer.render(classOfType)

    verify(classOfRenderer).render(eqTree(classOfType))
  }
}
