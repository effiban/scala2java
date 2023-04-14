package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

class CompositeAtLeastOneTruePredicate1Test extends UnitTestSuite {

  private val IncludedObj1 = TestObj("A")
  private val IncludedObj2 = TestObj("B")
  private val ExcludedObj1A = TestObj("ExcludedLib1A")
  private val ExcludedObj1B = TestObj("ExcludedLib1B")
  private val ExcludedObj2 = TestObj("ExcludedLib2")

  private val Arg = TestArg("myArg")

  private val ObjExcludedScenarios = Table(
    ("Desc", "Obj", "ExpectedExcluded"),
    ("IncludedObj1", IncludedObj1, false),
    ("IncludedObj2", IncludedObj2, false),
    ("ExcludedLibraryObj1A", ExcludedObj1A, true),
    ("ExcludedLibraryObj1B", ExcludedObj1B, true),
    ("ExcludedLibraryObj2", ExcludedObj2, true)
  )

  private val predicate1 = mock[TestPredicate]
  private val predicate2 = mock[TestPredicate]

  private val compositePredicate = new CompositeAtLeastOneTruePredicate1[TestObj, TestArg] {
    override protected val predicates: List[(TestObj, TestArg) => Boolean] = List(predicate1, predicate2)
  }

  override def beforeEach(): Unit = {
    when(predicate1.apply(ExcludedObj1A, Arg)).thenReturn(true)
    when(predicate1.apply(ExcludedObj1B, Arg)).thenReturn(true)
    when(predicate2.apply(ExcludedObj2, Arg)).thenReturn(true)
  }

  forAll(ObjExcludedScenarios) { (desc: String, testObj: TestObj, expectedExcluded: Boolean) =>
    test(s"""The Obj '$desc' should be ${if (expectedExcluded) "excluded" else "included"}""") {
      compositePredicate.apply(testObj, Arg) shouldBe expectedExcluded
    }
  }

  private case class TestObj(name: String)

  private case class TestArg(name: String)

  private trait TestPredicate extends ((TestObj, TestArg) => Boolean)
}
