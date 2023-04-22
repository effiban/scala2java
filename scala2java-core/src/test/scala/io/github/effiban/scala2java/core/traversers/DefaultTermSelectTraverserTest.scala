package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class DefaultTermSelectTraverserTest extends UnitTestSuite {
  private val qualifier = q"myObj"
  private val name = q"myMember"

  private val qualifierTraverser = mock[TermTraverser]
  private val termNameRenderer = mock[TermNameRenderer]

  private val termSelectTraverser = new DefaultTermSelectTraverserImpl(
    qualifierTraverser,
    termNameRenderer
  )

  test("traverse()") {
    val termSelect = q"myObj.myMember"

    doWrite("myObj").when(qualifierTraverser).traverse(eqTree(qualifier))
    doWrite("myMember").when(termNameRenderer).render(eqTree(name))

    termSelectTraverser.traverse(termSelect)

    outputWriter.toString shouldBe "myObj.myMember"
  }
}
