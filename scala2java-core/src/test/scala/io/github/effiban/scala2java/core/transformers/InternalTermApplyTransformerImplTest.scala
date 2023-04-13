package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier
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

  private val termNameClassifier = mock[TermNameClassifier]
  private val termApplyTransformer = mock[TermApplyTransformer]

  private val internalTermApplyTransformer = new InternalTermApplyTransformerImpl(termApplyTransformer, termNameClassifier)

  test("transform() of an untyped method invocation of an implicit 'apply()', should add the 'apply()' and call inner transformer") {
    val termName = q"MyObject"
    val termApply = q"MyObject(1)"
    val adjustedTermApply = q"MyObject.apply(1)"
    val expectedTransformedTermApply = q"MyObject.create(1)"

    when(termNameClassifier.hasApplyMethod(eqTree(termName))).thenReturn(true)
    when(termApplyTransformer.transform(eqTree(adjustedTermApply), eqTermApplyTransformationContext(Context)))
      .thenReturn(expectedTransformedTermApply)

    internalTermApplyTransformer.transform(termApply, Context).structure shouldBe expectedTransformedTermApply.structure
  }

  test("transform() of an typed method invocation of an implicit 'apply()', should add the 'apply()' and call inner transformer") {
    val termName = q"MyObject"
    val termApply = q"MyObject[Int](1)"
    val adjustedTermApply = q"MyObject.apply[Int](1)"
    val expectedTransformedTermApply = q"MyObject.create[Int](1)"

    when(termNameClassifier.hasApplyMethod(eqTree(termName))).thenReturn(true)
    when(termApplyTransformer.transform(eqTree(adjustedTermApply), eqTermApplyTransformationContext(Context)))
      .thenReturn(expectedTransformedTermApply)

    internalTermApplyTransformer.transform(termApply, Context).structure shouldBe expectedTransformedTermApply.structure
  }

  test("transform() of an untyped method invocation with no implicit 'apply()', should call inner transformer directly") {
    val termName = q"myMethod"
    val termApply = q"myMethod(1)"
    val expectedTransformedTermApply = q"myTransformedMethod(1)"

    when(termNameClassifier.hasApplyMethod(eqTree(termName))).thenReturn(false)
    when(termApplyTransformer.transform(eqTree(termApply), eqTermApplyTransformationContext(Context)))
      .thenReturn(expectedTransformedTermApply)

    internalTermApplyTransformer.transform(termApply, Context).structure shouldBe expectedTransformedTermApply.structure
  }

  test("transform() of a typed method invocation with no implicit 'apply()', should call inner transformer directly") {
    val termName = q"myMethod"
    val termApply = q"myMethod[Int](1)"
    val expectedTransformedTermApply = q"myTransformedMethod[Int](1)"

    when(termNameClassifier.hasApplyMethod(eqTree(termName))).thenReturn(false)
    when(termApplyTransformer.transform(eqTree(termApply), eqTermApplyTransformationContext(Context)))
      .thenReturn(expectedTransformedTermApply)

    internalTermApplyTransformer.transform(termApply, Context).structure shouldBe expectedTransformedTermApply.structure
  }

  test("transform() of a method invocation with a qualified name, should call inner transformer directly") {
    val termApply = q"MyObj.myMethod(1)"
    val expectedTransformedTermApply = q"MyObj.myTransformedMethod(1)"

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

    when(termNameClassifier.hasApplyMethod(eqTree(fun))).thenReturn(false)
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

    when(termNameClassifier.hasApplyMethod(eqTree(fun))).thenReturn(false)
    when(termApplyTransformer.transform(eqTree(expectedFlattenedTermApply), eqTermApplyTransformationContext(Context)))
      .thenReturn(expectedTransformedTermApply)

    internalTermApplyTransformer.transform(nestedTermApply, Context).structure shouldBe expectedTransformedTermApply.structure
  }

  test("transform() of a Term.Function (lambda) invocation should convert it into a qualified expression with apply(), and then call inner transformer") {
    val lambdaInvocation = q"((x: Int) => x + 1)(2)"
    val expectedAdjustedLambdaInvocation = q"((x: Int) => x + 1).apply(2)"
    val expectedTransformedLambdaInvocation = q"((Function<Int, Int>)((x: Int) => x + 1)).apply(2)"

    when(termApplyTransformer.transform(eqTree(expectedAdjustedLambdaInvocation), eqTermApplyTransformationContext(Context)))
      .thenReturn(expectedTransformedLambdaInvocation)

    internalTermApplyTransformer.transform(lambdaInvocation, Context).structure shouldBe expectedTransformedLambdaInvocation.structure
  }
}
