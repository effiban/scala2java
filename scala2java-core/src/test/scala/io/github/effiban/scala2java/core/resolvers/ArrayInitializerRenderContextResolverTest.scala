package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.entities.{TermSelects, TypeSelects}
import io.github.effiban.scala2java.core.matchers.ArrayInitializerSizeRenderContextScalatestMatcher.equalArrayInitializerSizeRenderContext
import io.github.effiban.scala2java.core.renderers.contexts.{ArrayInitializerSizeRenderContext, ArrayInitializerValuesRenderContext}
import io.github.effiban.scala2java.core.renderers.matchers.ArrayInitializerValuesRenderContextScalatestMatcher.equalArrayInitializerValuesRenderContext
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerRenderContextResolver.tryResolve
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.{Init, Lit, Name, Term, Type, XtensionQuasiquoteType}

class ArrayInitializerRenderContextResolverTest extends UnitTestSuite {

  test("""tryResolve() for a 'Term.Apply' of 'scala.Array[String]("a", "b")' should return a context with type 'String' and the values""") {
    val args = List(Lit.String("a"), Lit.String("b"))
    val termApply = Term.Apply(Term.ApplyType(TermSelects.ScalaArray, List(t"String")), args)

    val expectedContext = ArrayInitializerValuesRenderContext(tpe = t"String", values = args)

    tryResolve(termApply).value should equalArrayInitializerValuesRenderContext(expectedContext)
  }

  test("tryResolve() for 'Term.Apply' of 'scala.Array[String]()' should return a context with type 'String' and no values") {
    val termApply = Term.Apply(Term.ApplyType(TermSelects.ScalaArray, List(t"String")), Nil)

    val expectedContext = ArrayInitializerValuesRenderContext(tpe = t"String")

    tryResolve(termApply).value should equalArrayInitializerValuesRenderContext(expectedContext)
  }

  test("tryResolve() for a 'Term.Apply' of 'scala.collection.immutable.List(1)' should return None") {
    val termApply = Term.Apply(TermSelects.ScalaList, List(Lit.Int(1)))

    tryResolve(termApply) shouldBe None
  }

  test("""tryResolve() for an 'Init' of 'scala.Array[String](3)' should return a context with type 'String' and size 3""") {
    val arg = Lit.Int(3)
    val init = Init(
      tpe = Type.Apply(TypeSelects.ScalaArray, List(t"String")),
      name = Name.Anonymous(),
      argss = List(List(arg))
    )

    val expectedContext = ArrayInitializerSizeRenderContext(tpe = t"String", size = arg)

    tryResolve(init).value should equalArrayInitializerSizeRenderContext(expectedContext)
  }

  test("""tryResolve() for an 'Init' of 'scala.Array[String]()' should return a context with type 'String' and size 0""") {
    val init = Init(
      tpe = Type.Apply(TypeSelects.ScalaArray, List(t"String")),
      name = Name.Anonymous(),
      argss = List(Nil)
    )

    val expectedContext = ArrayInitializerSizeRenderContext(tpe = t"String")

    tryResolve(init).value should equalArrayInitializerSizeRenderContext(expectedContext)
  }

  test("""tryResolve() for an 'Init' of 'scala.Array(3)' should return a context with type 'Object' and size 3""") {
    val arg = Lit.Int(3)
    val init = Init(
      tpe = TypeSelects.ScalaArray,
      name = Name.Anonymous(),
      argss = List(List(arg))
    )

    val expectedContext = ArrayInitializerSizeRenderContext(tpe = t"Object", size = arg)

    tryResolve(init).value should equalArrayInitializerSizeRenderContext(expectedContext)
  }

  test("""tryResolve() for an 'Init' of 'scala.Array()' should return a context with type 'Object' and size 0""") {
    val init = Init(
      tpe = TypeSelects.ScalaArray,
      name = Name.Anonymous(),
      argss = List(Nil)
    )

    val expectedContext = ArrayInitializerSizeRenderContext(tpe = t"Object")

    tryResolve(init).value should equalArrayInitializerSizeRenderContext(expectedContext)
  }

  test("""tryResolve() for an 'Init' of 'List(3)' should return None""") {
    val arg = Lit.Int(3)
    val init = Init(
      tpe = t"List",
      name = Name.Anonymous(),
      argss = List(List(arg))
    )

    tryResolve(init) shouldBe None
  }

}
