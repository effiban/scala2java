package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class DefaultTermSelectTraverserTest extends UnitTestSuite {
  private val defaultTermRefTraverser = mock[DefaultTermRefTraverser]
  private val termNameRenderer = mock[TermNameRenderer]

  private val termSelectTraverser = new DefaultTermSelectTraverserImpl(
    defaultTermRefTraverser,
    termNameRenderer
  )

  test("traverse() when qualifier is a Term.Name") {
    val qualifier = q"myObj"
    val name = q"myMember"
    val termSelect = q"myObj.myMember"

    doWrite("myObj").when(defaultTermRefTraverser).traverse(eqTree(qualifier))
    doWrite("myMember").when(termNameRenderer).render(eqTree(name))

    termSelectTraverser.traverse(termSelect)

    outputWriter.toString shouldBe "myObj.myMember"
  }

  test("traverse() when qualifier is a Term.Select") {
    val qualifier = q"myObj1.myObj2"
    val name = q"myMember"
    val termSelect = q"myObj1.myObj2.myMember"

    doWrite("myObj1.myObj2").when(defaultTermRefTraverser).traverse(eqTree(qualifier))
    doWrite("myMember").when(termNameRenderer).render(eqTree(name))

    termSelectTraverser.traverse(termSelect)

    outputWriter.toString shouldBe "myObj1.myObj2.myMember"
  }
}
