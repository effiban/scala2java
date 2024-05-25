package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.transformers.TermApplyInfixToTermApplyTransformer
import org.mockito.ArgumentMatchersSugar.any

import scala.meta.{Term, XtensionQuasiquoteTerm}

class InternalTermApplyInfixTransformerImplTest extends UnitTestSuite {

  private val termApplyInfixToTermApplyTransformer = mock[TermApplyInfixToTermApplyTransformer]
  private val treeTransformer = mock[TreeTransformer]

  private val internalTermApplyInfixTransformer = new InternalTermApplyInfixTransformerImpl(
    termApplyInfixToTermApplyTransformer,
    treeTransformer
  )

  test("transform() when transforms to Term.Apply") {
    val termApplyInfix = q"a fun b"
    val termApply = q"fun(a, b)"
    val transformedTermApply = q"funfun(aa, bb)"

    doReturn(Some(termApply)).when(termApplyInfixToTermApplyTransformer).transform(termApplyInfix)
    doReturn(transformedTermApply).when(treeTransformer).transform(termApply)

    internalTermApplyInfixTransformer.transform(termApplyInfix).structure shouldBe transformedTermApply.structure
  }

  test("transform() when does not transform to Term.Apply") {
    val termApplyInfix = q"a fun b"
    val transformedTermApplyInfix = q"aa fun bb"

    doReturn(None).when(termApplyInfixToTermApplyTransformer).transform(termApplyInfix)

    doAnswer((arg: Term) => arg match {
      case q"a" => q"aa"
      case q"b" => q"bb"
      case other => other
    }).when(treeTransformer).transform(any[Term])

    internalTermApplyInfixTransformer.transform(termApplyInfix).structure shouldBe transformedTermApplyInfix.structure
  }
}
