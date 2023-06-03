package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.contexts.{ArrayInitializerSizeRenderContext, ArrayInitializerValuesRenderContext}
import io.github.effiban.scala2java.core.matchers.ArrayInitializerSizeRenderContextScalatestMatcher.equalArrayInitializerSizeRenderContext
import io.github.effiban.scala2java.core.matchers.ArrayInitializerValuesRenderContextScalatestMatcher.equalArrayInitializerValuesRenderContext
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerRenderContextResolver.tryResolve
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}

import scala.meta.{Init, Lit, Name, Term, Type}

class ArrayInitializerRenderContextResolverTest extends UnitTestSuite {
  test("""tryResolve() for a 'Term.Apply' of 'Array[String]("a", "b")' should return a context with type 'String' and the values""") {
    val args = List(Lit.String("a"), Lit.String("b"))
    val termApply = Term.Apply(Term.ApplyType(TermNames.ScalaArray, List(TypeNames.String)), args)

    val expectedContext = ArrayInitializerValuesRenderContext(tpe = TypeNames.String, values = args)

    tryResolve(termApply).value should equalArrayInitializerValuesRenderContext(expectedContext)
  }

  test("tryResolve() for 'Term.Apply' of 'Array[String]()' should return a context with type 'String' and no values") {
    val termApply = Term.Apply(Term.ApplyType(TermNames.ScalaArray, List(TypeNames.String)), Nil)

    val expectedContext = ArrayInitializerValuesRenderContext(tpe = TypeNames.String)

    tryResolve(termApply).value should equalArrayInitializerValuesRenderContext(expectedContext)
  }

  test("tryResolve() for a 'Term.Apply' of 'List(1)' should return None") {
    val termApply = Term.Apply(TermNames.List, List(Lit.Int(1)))

    tryResolve(termApply) shouldBe None
  }

  test("""tryResolve() for an 'Init' of 'Array[String](3)' should return a context with type 'String' and size 3""") {
    val arg = Lit.Int(3)
    val init = Init(
      tpe = Type.Apply(TypeNames.ScalaArray, List(TypeNames.String)),
      name = Name.Anonymous(),
      argss = List(List(arg))
    )

    val expectedContext = ArrayInitializerSizeRenderContext(tpe = TypeNames.String, size = arg)

    tryResolve(init).value should equalArrayInitializerSizeRenderContext(expectedContext)
  }

  test("""tryResolve() for an 'Init' of 'Array[String]()' should return a context with type 'String' and size 0""") {
    val init = Init(
      tpe = Type.Apply(TypeNames.ScalaArray, List(TypeNames.String)),
      name = Name.Anonymous(),
      argss = List(Nil)
    )

    val expectedContext = ArrayInitializerSizeRenderContext(tpe = TypeNames.String)

    tryResolve(init).value should equalArrayInitializerSizeRenderContext(expectedContext)
  }

  test("""tryResolve() for an 'Init' of 'List(3)' should return None""") {
    val arg = Lit.Int(3)
    val init = Init(
      tpe = TypeNames.List,
      name = Name.Anonymous(),
      argss = List(List(arg))
    )

    tryResolve(init) shouldBe None
  }

}
