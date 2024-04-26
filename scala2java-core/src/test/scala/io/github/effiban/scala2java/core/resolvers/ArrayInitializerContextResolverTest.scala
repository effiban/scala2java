package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.contexts.{ArrayInitializerSizeContext, ArrayInitializerValuesContext}
import io.github.effiban.scala2java.core.entities.TermSelects
import io.github.effiban.scala2java.core.matchers.ArrayInitializerSizeContextScalatestMatcher.equalArrayInitializerSizeContext
import io.github.effiban.scala2java.core.matchers.ArrayInitializerValuesContextScalatestMatcher.equalArrayInitializerValuesContext
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerContextResolver.tryResolve
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}

import scala.meta.{Init, Lit, Name, Term, Type}

class ArrayInitializerContextResolverTest extends UnitTestSuite {

  test("""tryResolve() for a 'Term.Apply' of 'scala.Array[String]("a", "b")' should return a context with type 'String' and the values""") {
    val args = List(Lit.String("a"), Lit.String("b"))
    val termApply = Term.Apply(Term.ApplyType(TermSelects.ScalaArray, List(TypeNames.String)), args)

    val expectedContext = ArrayInitializerValuesContext(maybeType = Some(TypeNames.String), values = args)

    tryResolve(termApply).value should equalArrayInitializerValuesContext(expectedContext)
  }

  test("""tryResolve() for 'Term.Apply' of 'scala.Array("a", "b")' should return a context with no type and the values""") {
    val args = List(Lit.String("a"), Lit.String("b"))
    val termApply = Term.Apply(TermSelects.ScalaArray, args)

    val expectedContext = ArrayInitializerValuesContext(maybeType = None, values = args)

    tryResolve(termApply).value should equalArrayInitializerValuesContext(expectedContext)
  }

  test("tryResolve() for 'Term.Apply' of 'scala.Array()' should return the default context") {
    val termApply = Term.Apply(TermSelects.ScalaArray, Nil)

    tryResolve(termApply).value should equalArrayInitializerValuesContext(ArrayInitializerValuesContext())
  }

  test("tryResolve() for a 'Term.Apply' of 'scala.List(1)' should return None") {
    val termApply = Term.Apply(TermSelects.ScalaList, List(Lit.Int(1)))

    tryResolve(termApply) shouldBe None
  }

  test("""tryResolve() for an 'Init' of 'Array[String](3)' should return a context with type 'String' and size 3""") {
    val arg = Lit.Int(3)
    val init = Init(
      tpe = Type.Apply(TypeNames.ScalaArray, List(TypeNames.String)),
      name = Name.Anonymous(),
      argss = List(List(arg))
    )

    val expectedContext = ArrayInitializerSizeContext(tpe = TypeNames.String, size = arg)

    tryResolve(init).value should equalArrayInitializerSizeContext(expectedContext)
  }

  test("""tryResolve() for an 'Init' of 'Array(3)' should return a context with type 'Any' and size 3""") {
    val arg = Lit.Int(3)
    val init = Init(
      tpe = TypeNames.ScalaArray,
      name = Name.Anonymous(),
      argss = List(List(arg))
    )

    val expectedContext = ArrayInitializerSizeContext(size = arg)

    tryResolve(init).value should equalArrayInitializerSizeContext(expectedContext)
  }

  test("""tryResolve() for an 'Init' of 'Array()' should return the default context""") {
    val init = Init(
      tpe = TypeNames.ScalaArray,
      name = Name.Anonymous(),
      argss = List(Nil)
    )

    tryResolve(init).value should equalArrayInitializerSizeContext(ArrayInitializerSizeContext())
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
