package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.entities.ArgumentCoordinates
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteTerm

class InvocationArgClassifierTest extends UnitTestSuite {

  private val ArgumentContextClassifications = Table(
    ("ArgumentCoordinates", "ExpectedPassedByName"),
    (ArgumentCoordinates(parent = q"Try.apply(x)", index = 0), true),
    (ArgumentCoordinates(parent = q"Try.apply[Int](x)", index = 0), true),
    (ArgumentCoordinates(parent = q"Future.apply(x)", index = 0), true),
    (ArgumentCoordinates(parent = q"Future.apply[Int](x)", index = 0), true),
    (ArgumentCoordinates(parent = q"Some(x)", maybeName = Some(q"value"), index = 0), false),
    (ArgumentCoordinates(parent = q"Some(x)", index = 0), false),
    (ArgumentCoordinates(parent = q"Right(x)", index = 0), false),
    (ArgumentCoordinates(parent = q"foo(x)", index = 0), false),
    (ArgumentCoordinates(parent = q"foo(x)", index = 1), false)
  )

  forAll(ArgumentContextClassifications) { case (argCoords: ArgumentCoordinates, expectedPassedByName: Boolean) =>
    test(s"The argument at: $argCoords is passed by ${if (expectedPassedByName) "name" else "value"}") {
      InvocationArgClassifier.isPassedByName(argCoords)
    }
  }
}
