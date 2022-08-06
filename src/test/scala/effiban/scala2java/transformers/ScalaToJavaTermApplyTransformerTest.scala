package effiban.scala2java.transformers

import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TermNames.{JavaIntStreamTermName, JavaRangeTermName, ScalaRangeTermName}

import scala.meta.{Lit, Term}

class ScalaToJavaTermApplyTransformerTest extends UnitTestSuite {

  test("transform() of Range(...) should return IntStream.range(...)") {
    val scalaTermApply = Term.Apply(ScalaRangeTermName, List(Lit.Int(0), Lit.Int(10)))
    val expectedRangeTermApply = Term.Apply(Term.Select(JavaIntStreamTermName, JavaRangeTermName), List(Lit.Int(0), Lit.Int(10)))

    ScalaToJavaTermApplyTransformer.transform(scalaTermApply).structure shouldBe expectedRangeTermApply.structure
  }

  test("transform() of dummy(...) should return the same") {
    val termApply = Term.Apply(Term.Name("dummy"), List(Lit.Int(1)))

    ScalaToJavaTermApplyTransformer.transform(termApply).structure shouldBe termApply.structure
  }
}
