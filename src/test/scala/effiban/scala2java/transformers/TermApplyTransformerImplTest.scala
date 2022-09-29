package effiban.scala2java.transformers

import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames

import scala.meta.{Lit, Term}

class TermApplyTransformerImplTest extends UnitTestSuite {

  private val fun = Term.Name("foo")
  private val arg1 = Term.Name("arg1")
  private val arg2 = Term.Name("arg2")
  private val arg3 = Term.Name("arg3")
  private val arg4 = Term.Name("arg4")
  private val arg5 = Term.Name("arg5")
  private val arg6 = Term.Name("arg6")

  private val termApplyNameTransformer = mock[TermApplyNameTransformer]

  private val termApplyTransformer = new TermApplyTransformerImpl(termApplyNameTransformer)

  test("transform() of a Term.Apply(Term.Name, _) should return same object with the name transformed") {
    val initialTermApply = Term.Apply(Term.Name("input"), List(Lit.Int(1)))
    val expectedTermApply = Term.Apply(Term.Name("output"), List(Lit.Int(1)))

    when(termApplyNameTransformer.transform(eqTree(Term.Name("input")))).thenReturn(Term.Name("output"))

    termApplyTransformer.transform(initialTermApply).structure shouldBe expectedTermApply.structure
  }

  test("transform() of a Term.Apply(Term.ApplyType(Term.Name, _), _) should return same object with the name transformed") {
    val initialTermApply = Term.Apply(Term.ApplyType(Term.Name("input"), List(TypeNames.Int)), List(Lit.Int(1)))
    val expectedTermApply = Term.Apply(Term.ApplyType(Term.Name("output"), List(TypeNames.Int)), List(Lit.Int(1)))

    when(termApplyNameTransformer.transform(eqTree(Term.Name("input")))).thenReturn(Term.Name("output"))

    termApplyTransformer.transform(initialTermApply).structure shouldBe expectedTermApply.structure
  }

  test("transform() of a Term.Apply(Term.Select(_, _), _) should return same object") {
    val termApply = Term.Apply(Term.Select(Term.Name("a"), Term.Name("b")), List(Lit.Int(1)))

    termApplyTransformer.transform(termApply).structure shouldBe termApply.structure

  }

    // foo(arg1, arg2)(arg3, arg4) ---> foo(arg1, arg2, arg3, arg4)
  test("transform() of a 2-level nested Apply should return a single Apply with concatenated args") {
    val scalaStyleTermApply =
      Term.Apply(
        Term.Apply(fun, List(arg1, arg2)),
        List(arg3, arg4)
    )
    val expectedJavaStyleTermApply = Term.Apply(fun, List(arg1, arg2, arg3, arg4))

    when(termApplyNameTransformer.transform(eqTree(fun))).thenReturn(fun)

    termApplyTransformer.transform(scalaStyleTermApply).structure shouldBe expectedJavaStyleTermApply.structure
  }

  // foo(arg1, arg2)(arg3, arg4)(arg5, arg6) ---> foo(arg1, arg2, arg3, arg4, arg5, arg6)
  test("transform() of a 3-level nested Apply should return a single Apply with concatenated args") {
    val scalaStyleTermApply =
      Term.Apply(
        Term.Apply(
          Term.Apply(fun, List(arg1, arg2)),
          List(arg3, arg4)),
        List(arg5, arg6)
      )
    val expectedJavaStyleTermApply = Term.Apply(fun, List(arg1, arg2, arg3, arg4, arg5, arg6))

    when(termApplyNameTransformer.transform(eqTree(fun))).thenReturn(fun)

    termApplyTransformer.transform(scalaStyleTermApply).structure shouldBe expectedJavaStyleTermApply.structure
  }
}
