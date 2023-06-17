package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class CompositeApplyTypeRendererImplTest extends UnitTestSuite {

  private val classOfRenderer = mock[ClassOfRenderer]
  private val standardApplyTypeRenderer = mock[StandardApplyTypeRenderer]

  private val applyTypeRenderer = new CompositeApplyTypeRendererImpl(classOfRenderer, standardApplyTypeRenderer)

  test("render() for classOf[..]") {
    val classOfType = q"classOf[T]"

    applyTypeRenderer.render(classOfType)

    verify(classOfRenderer).render(eqTree(classOfType))
  }

  test("render() for myClass.myMethod[T]") {
    val applyType = q"myClass.myMethod[T]"

    applyTypeRenderer.render(applyType)

    verify(standardApplyTypeRenderer).render(eqTree(applyType))
  }
}
