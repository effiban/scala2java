package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.ClassOfRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class DeprecatedMainApplyTypeTraverserImplTest extends UnitTestSuite {

  private val classOfTraverser = mock[ClassOfTraverser]
  private val classOfRenderer = mock[ClassOfRenderer]
  private val standardApplyTypeTraverser = mock[DeprecatedStandardApplyTypeTraverser]

  private val mainApplyTypeTraverser = new DeprecatedMainApplyTypeTraverserImpl(
    classOfTraverser,
    classOfRenderer,
    standardApplyTypeTraverser
  )


  test("traverse() when function is 'classOf' should call the dedicated traverser") {
    val classOfType = q"classOf[T]"
    val traversedClassOfType = q"classOf[U]"

    doReturn(traversedClassOfType).when(classOfTraverser).traverse(eqTree(classOfType))

    mainApplyTypeTraverser.traverse(classOfType)

    verify(classOfRenderer).render(eqTree(traversedClassOfType))
  }

  test("traverse() when function is 'foo', should call the standard traverser") {
    val termApplyType = q"foo[T1, T2]"

    mainApplyTypeTraverser.traverse(termApplyType)

    verify(standardApplyTypeTraverser).traverse(eqTree(termApplyType))
  }
}
