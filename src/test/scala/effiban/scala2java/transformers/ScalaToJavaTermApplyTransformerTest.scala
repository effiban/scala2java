package effiban.scala2java.transformers

import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TermNames.{JavaIntStreamTermName, JavaRangeTermName, ScalaRangeTermName}

import scala.meta.{Lit, Term}

class ScalaToJavaTermApplyTransformerTest extends UnitTestSuite {

  private val fun = Term.Name("foo")
  private val arg1 = Term.Name("arg1")
  private val arg2 = Term.Name("arg2")
  private val arg3 = Term.Name("arg3")
  private val arg4 = Term.Name("arg4")
  private val arg5 = Term.Name("arg5")
  private val arg6 = Term.Name("arg6")


  test("transform() of Range(...) should return IntStream.range(...)") {
    val scalaTermApply = Term.Apply(ScalaRangeTermName, List(Lit.Int(0), Lit.Int(10)))
    val expectedRangeTermApply = Term.Apply(Term.Select(JavaIntStreamTermName, JavaRangeTermName), List(Lit.Int(0), Lit.Int(10)))

    ScalaToJavaTermApplyTransformer.transform(scalaTermApply).structure shouldBe expectedRangeTermApply.structure
  }

  test("transform() of dummy(...) should return the same") {
    val termApply = Term.Apply(Term.Name("dummy"), List(Lit.Int(1)))

    ScalaToJavaTermApplyTransformer.transform(termApply).structure shouldBe termApply.structure
  }

  // foo(arg1, arg2)(arg3, arg4) ---> foo(arg1, arg2, arg3, arg4)
  test("transform() of a 2-level nested Apply should return a single Apply with concatenated args") {
    val scalaStyleTermApply =
      Term.Apply(
        Term.Apply(fun, List(arg1, arg2)),
        List(arg3, arg4)
    )
    val expectedJavaStyleTermApply = Term.Apply(fun, List(arg1, arg2, arg3, arg4))

    ScalaToJavaTermApplyTransformer.transform(scalaStyleTermApply).structure shouldBe expectedJavaStyleTermApply.structure
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

    ScalaToJavaTermApplyTransformer.transform(scalaStyleTermApply).structure shouldBe expectedJavaStyleTermApply.structure
  }
}
