package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.PatSeqWildcardRenderer
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Pat

class PatSeqWildcardTraverserImplTest extends UnitTestSuite {

  private val patSeqWildcardRenderer = mock[PatSeqWildcardRenderer]
  private val patSeqWildcardTraverser = new PatSeqWildcardTraverserImpl(patSeqWildcardRenderer)

  test("traverse()") {
    patSeqWildcardTraverser.traverse(Pat.SeqWildcard())

    verify(patSeqWildcardRenderer).render(eqTree(Pat.SeqWildcard()))
  }
}
