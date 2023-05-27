package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.matchers.TermApplyTransformationContextMockitoMatcher.eqTermApplyTransformationContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext
import io.github.effiban.scala2java.spi.transformers.TermApplyTransformer
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Term, XtensionQuasiquoteTerm, XtensionQuasiquoteType}

class InternalTermApplyTransformerImplTest extends UnitTestSuite {

  private val fun = Term.Name("foo")
  private val transformedFun = Term.Name("transformedFoo")
  private val arg1 = Term.Name("arg1")
  private val arg2 = Term.Name("arg2")
  private val arg3 = Term.Name("arg3")
  private val arg4 = Term.Name("arg4")
  private val arg5 = Term.Name("arg5")
  private val arg6 = Term.Name("arg6")

  private val Context = TermApplyTransformationContext(maybeParentType = Some(t"Parent"))

  private val termApplyTransformer = mock[TermApplyTransformer]

  private val internalTermApplyTransformer = new InternalTermApplyTransformerImpl(termApplyTransformer)


  test("transform() of a method invocation with a single argument list, should call inner transformer directly") {
    val termName = q"myMethod"
    val termApply = q"myMethod(1)"
    val expectedTransformedTermApply = q"myTransformedMethod(1)"

    when(termApplyTransformer.transform(eqTree(termApply), eqTermApplyTransformationContext(Context)))
      .thenReturn(expectedTransformedTermApply)

    internalTermApplyTransformer.transform(termApply, Context).structure shouldBe expectedTransformedTermApply.structure
  }


  // foo(arg1, arg2)(arg3, arg4) ---> foo(arg1, arg2, arg3, arg4) ---> transformedFoo(arg1, arg2, arg3, arg4)
  test("transform() of a 2-level nested invocation, should convert to a single invocation with concatenated args and call inner transformer") {
    val nestedTermApply =
      Term.Apply(
        Term.Apply(fun, List(arg1, arg2)),
        List(arg3, arg4)
    )
    val expectedFlattenedTermApply = Term.Apply(fun, List(arg1, arg2, arg3, arg4))
    val expectedTransformedTermApply = Term.Apply(transformedFun, List(arg1, arg2, arg3, arg4))

    when(termApplyTransformer.transform(eqTree(expectedFlattenedTermApply), eqTermApplyTransformationContext(Context)))
      .thenReturn(expectedTransformedTermApply)

    internalTermApplyTransformer.transform(nestedTermApply, Context).structure shouldBe expectedTransformedTermApply.structure
  }

  // foo(arg1, arg2)(arg3, arg4)(arg5, arg6) ---> foo(arg1, arg2, arg3, arg4, arg5, arg6) ---> transformedFoo(arg1, arg2, arg3, arg4, arg5, arg6)
  test("transform() of a 3-level nested invocation should convert tp a single invocation with concatenated args, and then call inner transformer") {
    val nestedTermApply =
      Term.Apply(
        Term.Apply(
          Term.Apply(fun, List(arg1, arg2)),
          List(arg3, arg4)),
        List(arg5, arg6)
      )
    val expectedFlattenedTermApply = Term.Apply(fun, List(arg1, arg2, arg3, arg4, arg5, arg6))
    val expectedTransformedTermApply = Term.Apply(transformedFun, List(arg1, arg2, arg3, arg4, arg5, arg6))

    when(termApplyTransformer.transform(eqTree(expectedFlattenedTermApply), eqTermApplyTransformationContext(Context)))
      .thenReturn(expectedTransformedTermApply)

    internalTermApplyTransformer.transform(nestedTermApply, Context).structure shouldBe expectedTransformedTermApply.structure
  }
}
