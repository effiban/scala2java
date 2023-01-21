package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.{TemplateTermApplyInfixToDefnTransformer, TemplateTermApplyToDefnTransformer}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class TemplateStatTransformerImplTest extends UnitTestSuite {

  private val termApplyInfixToDefnTransformer = mock[TemplateTermApplyInfixToDefnTransformer]
  private val termApplyToDefnTransformer = mock[TemplateTermApplyToDefnTransformer]

  private val templateStatTransformer = new TemplateStatTransformerImpl(termApplyInfixToDefnTransformer, termApplyToDefnTransformer)


  test("transform() a Term.ApplyInfix when inner transformer returns a result, should return it") {
    val termApplyInfix =
      q"""
    "My process" should "return success" {
      runMyProcess() shouldBe "success"
    }
    """

    val defnDef =
      q"""
    @Test
    @DisplayName("My process should return success")
    def myProcessShouldReturnSuccess(): Unit = {
      runMyProcess() shouldBe "success"
    }
    """

    when(termApplyInfixToDefnTransformer.transform(eqTree(termApplyInfix))).thenReturn(Some(defnDef))

    templateStatTransformer.transform(termApplyInfix).structure shouldBe defnDef.structure
  }

  test("transform() a Term.ApplyInfix when inner transformer returns None, should return the input") {
    val termApplyInfix =
      q"""
  "My process" should "return success" {
    runMyProcess() shouldBe "success"
  }
  """

    when(termApplyInfixToDefnTransformer.transform(eqTree(termApplyInfix))).thenReturn(None)

    templateStatTransformer.transform(termApplyInfix).structure shouldBe termApplyInfix.structure
  }

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

    when(termApplyToDefnTransformer.transform(eqTree(termApply))).thenReturn(Some(defnDef))

    templateStatTransformer.transform(termApply).structure shouldBe defnDef.structure
  }

  test("transform() a Term.Apply when inner transformer returns None, should return the input") {
    val termApply =
      q"""
    test("myTest") {
      checkSomething()
    }
    """

    when(termApplyToDefnTransformer.transform(eqTree(termApply))).thenReturn(None)

    templateStatTransformer.transform(termApply).structure shouldBe termApply.structure
  }

  test("transform() a Defn.Val should return return the input") {
    val defnVal = q"val myVal = 3"

    templateStatTransformer.transform(defnVal).structure shouldBe defnVal.structure
  }
}
