package effiban.scala2java.orderings

import effiban.scala2java.testsuites.UnitTestSuite

class JavaModifierOrderingTest extends UnitTestSuite {

  private val ModifierOrderingScenarios = Table(
    ("ModifierName1", "ModifierName2", "ExpectedResult"),
    ("private", "protected", 0),
    ("protected", "public", 0),
    ("public", "default", 0),
    ("default", "static", -1),
    ("static", "sealed", -1),
    ("sealed", "abstract", -1),
    ("abstract", "final", -1)
  )

  forAll(ModifierOrderingScenarios) { case (modifierName1: String, modifierName2: String, expectedResult: Int) =>
    test(s"'$modifierName1' ${comparisonResultToDescription(expectedResult)} '$modifierName2'") {
      JavaModifierOrdering.compare(modifierName1, modifierName2) shouldBe expectedResult
    }
  }

  private def comparisonResultToDescription(comparisonResult: Int) = {
    comparisonResult match {
      case 0 => "should be at the same position as"
      case result if result < 0  => "should precede"
      case _ => "should follow"
    }
  }
}
