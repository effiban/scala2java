package effiban.scala2java.transformers

import effiban.scala2java.classifiers.TermNameClassifier
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{TermNames, TypeNames}

import scala.meta.{Lit, Term}

class TermApplyTransformerImplTest extends UnitTestSuite {

  private val fun = Term.Name("foo")
  private val arg1 = Term.Name("arg1")
  private val arg2 = Term.Name("arg2")
  private val arg3 = Term.Name("arg3")
  private val arg4 = Term.Name("arg4")
  private val arg5 = Term.Name("arg5")
  private val arg6 = Term.Name("arg6")

  private val termNameClassifier = mock[TermNameClassifier]

  private val termApplyTransformer = new TermApplyTransformerImpl(termNameClassifier)

  test("transform() of a 'Future' invocation should return an invocation with 'apply' added and arg wrapped by a lambda") {
    val initialTermApply = Term.Apply(TermNames.Future, List(Lit.Int(1)))
    val expectedTermApply = Term.Apply(Term.Select(TermNames.Future, TermNames.Apply), List(Term.Function(Nil, Lit.Int(1))))

    when(termNameClassifier.isScalaObject(eqTree(TermNames.Future))).thenReturn(true)

    termApplyTransformer.transform(initialTermApply).structure shouldBe expectedTermApply.structure
  }

  test("transform() of a typed 'Future' invocation should return an invocation with 'apply' added and arg wrapped by a lambda") {
    val initialTermApply = Term.Apply(Term.ApplyType(TermNames.Future, List(TypeNames.Int)), List(Lit.Int(1)))
    val expectedTermApply = Term.Apply(
      Term.ApplyType(Term.Select(TermNames.Future, TermNames.Apply), List(TypeNames.Int)),
      List(Term.Function(Nil, Lit.Int(1)))
    )

    when(termNameClassifier.isScalaObject(eqTree(TermNames.Future))).thenReturn(true)

    termApplyTransformer.transform(initialTermApply).structure shouldBe expectedTermApply.structure
  }

  test("transform() of a untyped Scala object invocation should return an invocation with 'apply' added") {
    val initialTermApply = Term.Apply(Term.Name("ScalaObject"), List(Lit.Int(1)))
    val expectedTermApply = Term.Apply(Term.Select(Term.Name("ScalaObject"), TermNames.Apply), List(Lit.Int(1)))

    when(termNameClassifier.isScalaObject(eqTree(Term.Name("ScalaObject")))).thenReturn(true)

    termApplyTransformer.transform(initialTermApply).structure shouldBe expectedTermApply.structure
  }

  test("transform() of a typed Scala object invocation should return an invocation with 'apply' added") {
    val initialTermApply = Term.Apply(Term.ApplyType(Term.Name("ScalaObject"), List(TypeNames.Int)), List(Lit.Int(1)))
    val expectedTermApply = Term.Apply(Term.ApplyType(Term.Select(Term.Name("ScalaObject"), TermNames.Apply), List(TypeNames.Int)), List(Lit.Int(1)))

    when(termNameClassifier.isScalaObject(eqTree(Term.Name("ScalaObject")))).thenReturn(true)

    termApplyTransformer.transform(initialTermApply).structure shouldBe expectedTermApply.structure
  }

  test("transform() of an unqualified non-Scala object invocation should return the same invocation") {
    val termApply = Term.Apply(Term.Name("Foo"), List(Lit.Int(1)))

    when(termNameClassifier.isScalaObject(eqTree(Term.Name("Foo")))).thenReturn(false)

    termApplyTransformer.transform(termApply).structure shouldBe termApply.structure
  }


  test("transform() of a qualified-name invocation should return same invocation") {
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

    when(termNameClassifier.isScalaObject(eqTree(fun))).thenReturn(false)

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

    when(termNameClassifier.isScalaObject(eqTree(fun))).thenReturn(false)

    termApplyTransformer.transform(scalaStyleTermApply).structure shouldBe expectedJavaStyleTermApply.structure
  }
}
