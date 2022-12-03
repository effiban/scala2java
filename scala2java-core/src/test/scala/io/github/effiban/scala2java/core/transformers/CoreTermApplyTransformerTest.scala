package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.core.classifiers.TermNameClassifier
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{TermNames, TypeNames}

import scala.meta.{Lit, Term}

class CoreTermApplyTransformerTest extends UnitTestSuite {

  private val fun = Term.Name("foo")
  private val arg1 = Term.Name("arg1")
  private val arg2 = Term.Name("arg2")
  private val arg3 = Term.Name("arg3")
  private val arg4 = Term.Name("arg4")
  private val arg5 = Term.Name("arg5")
  private val arg6 = Term.Name("arg6")

  private val termNameClassifier = mock[TermNameClassifier]

  private val termApplyTransformer = new CoreTermApplyTransformer(termNameClassifier)

  test("transform() of a untyped pre-def object initializer, arg by-value, should return same with added 'apply'") {
    val initialTermApply = Term.Apply(Term.Name("ScalaObject"), List(Lit.Int(1)))
    val expectedTermApply = Term.Apply(Term.Select(Term.Name("ScalaObject"), TermNames.Apply), List(Lit.Int(1)))

    when(termNameClassifier.isPreDefScalaObject(eqTree(Term.Name("ScalaObject")))).thenReturn(true)

    termApplyTransformer.transform(initialTermApply).structure shouldBe expectedTermApply.structure
  }

  test("transform() of a typed pre-def object initializer, arg by-value, should return same with added 'apply'") {
    val initialTermApply = Term.Apply(Term.ApplyType(Term.Name("ScalaObject"), List(TypeNames.Int)), List(Lit.Int(1)))
    val expectedTermApply = Term.Apply(Term.ApplyType(Term.Select(Term.Name("ScalaObject"), TermNames.Apply), List(TypeNames.Int)), List(Lit.Int(1)))

    when(termNameClassifier.isPreDefScalaObject(eqTree(Term.Name("ScalaObject")))).thenReturn(true)

    termApplyTransformer.transform(initialTermApply).structure shouldBe expectedTermApply.structure
  }

  test("transform() of a untyped pre-def object initializer, arg by-name, should return same with added 'apply' and arg wrapped by lambda") {
    val initialTermApply = Term.Apply(TermNames.Future, List(Lit.Int(1)))
    val expectedTermApply = Term.Apply(Term.Select(TermNames.Future, TermNames.Apply), List(Term.Function(Nil, Lit.Int(1))))

    when(termNameClassifier.isPreDefScalaObject(eqTree(TermNames.Future))).thenReturn(true)
    when(termNameClassifier.isInstantiatedByName(eqTree(TermNames.Future))).thenReturn(true)

    termApplyTransformer.transform(initialTermApply).structure shouldBe expectedTermApply.structure
  }

  test("transform() of a typed pre-def object initializer, arg by-name, should return same with added 'apply' and arg wrapped by lambda") {
    val initialTermApply = Term.Apply(Term.ApplyType(TermNames.Future, List(TypeNames.Int)), List(Lit.Int(1)))
    val expectedTermApply = Term.Apply(
      Term.ApplyType(Term.Select(TermNames.Future, TermNames.Apply), List(TypeNames.Int)),
      List(Term.Function(Nil, Lit.Int(1)))
    )

    when(termNameClassifier.isPreDefScalaObject(eqTree(TermNames.Future))).thenReturn(true)
    when(termNameClassifier.isInstantiatedByName(eqTree(TermNames.Future))).thenReturn(true)

    termApplyTransformer.transform(initialTermApply).structure shouldBe expectedTermApply.structure
  }

  test("transform() of a regular method invocation with an unqualified name, should return  same invocation") {
    val termApply = Term.Apply(Term.Name("Foo"), List(Lit.Int(1)))

    when(termNameClassifier.isPreDefScalaObject(eqTree(Term.Name("Foo")))).thenReturn(false)

    termApplyTransformer.transform(termApply).structure shouldBe termApply.structure
  }


  test("transform() of a regular method invocation with a qualified name, should return same invocation") {
    val termApply = Term.Apply(Term.Select(Term.Name("a"), Term.Name("b")), List(Lit.Int(1)))

    termApplyTransformer.transform(termApply).structure shouldBe termApply.structure

  }

    // foo(arg1, arg2)(arg3, arg4) ---> foo(arg1, arg2, arg3, arg4)
  test("transform() of a 2-level nested invocation should return a single invocation with concatenated args") {
    val scalaStyleTermApply =
      Term.Apply(
        Term.Apply(fun, List(arg1, arg2)),
        List(arg3, arg4)
    )
    val expectedJavaStyleTermApply = Term.Apply(fun, List(arg1, arg2, arg3, arg4))

    when(termNameClassifier.isPreDefScalaObject(eqTree(fun))).thenReturn(false)

    termApplyTransformer.transform(scalaStyleTermApply).structure shouldBe expectedJavaStyleTermApply.structure
  }

  // foo(arg1, arg2)(arg3, arg4)(arg5, arg6) ---> foo(arg1, arg2, arg3, arg4, arg5, arg6)
  test("transform() of a 3-level nested invocation should return a single invocation with concatenated args") {
    val scalaStyleTermApply =
      Term.Apply(
        Term.Apply(
          Term.Apply(fun, List(arg1, arg2)),
          List(arg3, arg4)),
        List(arg5, arg6)
      )
    val expectedJavaStyleTermApply = Term.Apply(fun, List(arg1, arg2, arg3, arg4, arg5, arg6))

    when(termNameClassifier.isPreDefScalaObject(eqTree(fun))).thenReturn(false)

    termApplyTransformer.transform(scalaStyleTermApply).structure shouldBe expectedJavaStyleTermApply.structure
  }
}
