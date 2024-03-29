package io.github.effiban.scala2java.core.orderings

import io.github.effiban.scala2java.core.entities.JavaModifier
import io.github.effiban.scala2java.core.entities.JavaModifier.{Private, Protected}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

class JavaModifierOrderingTest extends UnitTestSuite {

  private val ModifierOrderingScenarios = Table(
    ("ModifierName1", "ModifierName2", "ExpectedResult"),
    (Private, Protected, 0),
    (JavaModifier.Protected, JavaModifier.Public, 0),
    (JavaModifier.Public, JavaModifier.Default, 0),
    (JavaModifier.Default, JavaModifier.Static, -1),
    (JavaModifier.Static, JavaModifier.Abstract, -1),
    (JavaModifier.Abstract, JavaModifier.Sealed, -1),
    (JavaModifier.Sealed, JavaModifier.NonSealed, 0),
    (JavaModifier.NonSealed, JavaModifier.Final, 0),
  )

  forAll(ModifierOrderingScenarios) { case (modifier1: JavaModifier, modifier2: JavaModifier, expectedResult: Int) =>
    test(s"'${modifier1.name}' ${comparisonResultToDescription(expectedResult)} '${modifier2.name}'") {
      JavaModifierOrdering.compare(modifier1, modifier2) shouldBe expectedResult
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
