package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.classifiers.TermTreeClassifier.isReturnable
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.Term.AnonymousFunction
import scala.meta.{Term, XtensionQuasiquoteTerm}

class TermTreeClassifierTest extends UnitTestSuite {

  private val Scenarios = Table(
    ("Term", "ExpectedReturnable"),
    (q"foo(1)", true),
    (q"a + b", true),
    (q"foo[Int]", true),
    (q"x: Int", true),
    (q"for (x <- xs) yield (x)", true),
    (q"x => foo(x)", true),
    (q"{case x: X => x }", true),
    (AnonymousFunction(q"foo"), true),
    (Term.Interpolate(q"s", List(q""""before-"""", q""""-after""""), List(q"myVal")), true),
    (q"3", true),
    (q"x", true),
    (q"new MyClass()", true),
    (q"new { foo() }", true),
    (q"x: _*", true),
    (q"a.b", true),
    (q"""x match { case 1 => "one" }""", true),
    (q"(x, 1)", true),
    (q"foo _", true),
    (q"throw new MyException()", false),
    (q"while (x < 3) do foo(x)", false)
  )

  forAll(Scenarios) { (term: Term, expectedReturnable: Boolean) =>
    test(s"isReturnable() for '$term' should return $expectedReturnable") {
      isReturnable(term) shouldBe expectedReturnable
    }
  }
}
