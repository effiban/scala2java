package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class DefaultTermSelectRendererImplTest extends UnitTestSuite {
  private val defaultTermRefRenderer = mock[DefaultTermRefRenderer]
  private val termNameRenderer = mock[TermNameRenderer]

  private val defaultTermSelectRenderer = new DefaultTermSelectRendererImpl(defaultTermRefRenderer, termNameRenderer)

  test("render() when qualifier is a Term.Name") {
    val qualifier = q"myObj"
    val name = q"myMember"
    val termSelect = q"myObj.myMember"

    doWrite("myObj").when(defaultTermRefRenderer).render(eqTree(qualifier))
    doWrite("myMember").when(termNameRenderer).render(eqTree(name))

    defaultTermSelectRenderer.render(termSelect)

    outputWriter.toString shouldBe "myObj.myMember"
  }

  test("render() when qualifier is a Term.Select") {
    val qualifier = q"myObj1.myObj2"
    val name = q"myMember"
    val termSelect = q"myObj1.myObj2.myMember"

    doWrite("myObj1.myObj2").when(defaultTermRefRenderer).render(eqTree(qualifier))
    doWrite("myMember").when(termNameRenderer).render(eqTree(name))

    defaultTermSelectRenderer.render(termSelect)

    outputWriter.toString shouldBe "myObj1.myObj2.myMember"
  }
}
