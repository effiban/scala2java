package effiban.scala2java.transformers

import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TermNames._

import scala.meta.Term

class TermSelectTransformerTest extends UnitTestSuite {

  test("transform Range.inclusive() should return IntStream.rangeClosed()") {
    val scalaTermSelect = Term.Select(ScalaRange, ScalaInclusive)
    val expectedJavaTermSelect = Term.Select(JavaIntStream, JavaRangeClosed)

    TermSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform Range.apply() should return IntStream.range()") {
    val scalaTermSelect = Term.Select(ScalaRange, Term.Name("apply"))
    val expectedJavaTermSelect = Term.Select(JavaIntStream, JavaRange)

    TermSelectTransformer.transform(scalaTermSelect).structure shouldBe expectedJavaTermSelect.structure
  }

  test("transform Dummy.dummy() should return the same") {
    val termSelect = Term.Select(Term.Name("Dummy"), Term.Name("dummy"))

    TermSelectTransformer.transform(termSelect).structure shouldBe termSelect.structure
  }
}
