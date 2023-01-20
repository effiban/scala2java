package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.TemplateTermApplyToDefnTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class TemplateStatTransformerImplTest extends UnitTestSuite {

  private val templateTermApplyToDefnTransformer = mock[TemplateTermApplyToDefnTransformer]

  private val templateStatTransformer = new TemplateStatTransformerImpl(templateTermApplyToDefnTransformer)


  test("transform() a Term.Apply when inner transformer returns a result, should return it") {
    val termApply =
      q"""
      test("myTest") {
        checkSomething()
      }
      """

    val defnDef =
      q"""
      @Test
      def myTest(): Unit = {
        checkSomething()
      }
      """

    when(templateTermApplyToDefnTransformer.transform(eqTree(termApply))).thenReturn(Some(defnDef))

    templateStatTransformer.transform(termApply).structure shouldBe defnDef.structure
  }

  test("transform() a Term.Apply when inner transformer returns None, should return the input") {
    val termApply =
      q"""
    test("myTest") {
      checkSomething()
    }
    """

    when(templateTermApplyToDefnTransformer.transform(eqTree(termApply))).thenReturn(None)

    templateStatTransformer.transform(termApply).structure shouldBe termApply.structure
  }

  test("transform() a Defn.Val should return return the input") {
    val defnVal = q"val myVal = 3"

    templateStatTransformer.transform(defnVal).structure shouldBe defnVal.structure
  }
}
