package effiban.scala2java.transformers

import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TermNames._

import scala.meta.Term

class ScalaToJavaTermSelectTransformerTest extends UnitTestSuite {

  test("transform Range.inclusive() should return IntStream.rangeClosed()") {
    val scalaTermSelect = Term.Select(ScalaRangeTermName, ScalaInclusiveTermName)
    val expectedJavaTermSelect = Term.Select(JavaIntStreamTermName, JavaRangeClosedTermName)

    ScalaToJavaTermSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform Range.apply() should return IntStream.range()") {
    val scalaTermSelect = Term.Select(ScalaRangeTermName, Term.Name("apply"))
    val expectedJavaTermSelect = Term.Select(JavaIntStreamTermName, JavaRangeTermName)

    ScalaToJavaTermSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform Dummy.dummy() should return the same") {
    val termSelect = Term.Select(Term.Name("Dummy"), Term.Name("dummy"))

    ScalaToJavaTermSelectTransformer.transform(termSelect).structure shouldBe termSelect.structure
  }
}
