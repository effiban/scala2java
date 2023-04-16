package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeApplyInfixRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteType

class TypeApplyInfixTraverserImplTest extends UnitTestSuite {
  private val typeApplyInfixRenderer = mock[TypeApplyInfixRenderer]

  private val typeApplyInfixTraverser = new TypeApplyInfixTraverserImpl(typeApplyInfixRenderer)

  test("traverser") {
    val typeApplyInfix = t"K Map V"

    typeApplyInfixTraverser.traverse(typeApplyInfix)

    verify(typeApplyInfixRenderer).render(eqTree(typeApplyInfix))
  }

}
