package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

class CompositeAtLeastOneTruePredicateTest extends UnitTestSuite {

  private val IncludedObj1 = TestObj("A")
  private val IncludedObj2 = TestObj("B")
  private val ExcludedObj1A = TestObj("ExcludedLib1A")
  private val ExcludedObj1B = TestObj("ExcludedLib1B")
  private val ExcludedObj2 = TestObj("ExcludedLib2")

  private val ObjExcludedScenarios = Table(
    ("Desc", "Obj", "ExpectedExcluded"),
    ("IncludedObj1", IncludedObj1, false),
    ("IncludedObj2", IncludedObj2, false),
    ("ExcludedLibraryObj1A", ExcludedObj1A, true),
    ("ExcludedLibraryObj1B", ExcludedObj1B, true),
    ("ExcludedLibraryObj2", ExcludedObj2, true)
  )

  private val predicate1 = mock[TestExcludedPredicate]
  private val predicate2 = mock[TestExcludedPredicate]

  private implicit val extensionRegistry: ExtensionRegistry = mock[ExtensionRegistry]

  private val compositePredicate = new CompositeAtLeastOneTruePredicate[TestObj] {
    override protected val predicates: List[TestObj => Boolean] = List(predicate1, predicate2)
  }

  override def beforeEach(): Unit = {
    when(predicate1.apply(ExcludedObj1A)).thenReturn(true)
    when(predicate1.apply(ExcludedObj1B)).thenReturn(true)
    when(predicate2.apply(ExcludedObj2)).thenReturn(true)
  }

  forAll(ObjExcludedScenarios) { (desc: String, testObj: TestObj, expectedExcluded: Boolean) =>
    test(s"""The Obj '$desc' should be ${if (expectedExcluded) "excluded" else "included"}""") {
      compositePredicate.apply(testObj) shouldBe expectedExcluded
    }
  }

  private case class TestObj(name: String)

  private trait TestExcludedPredicate extends (TestObj => Boolean)
}
