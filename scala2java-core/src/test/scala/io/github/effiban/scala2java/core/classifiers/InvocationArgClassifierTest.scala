package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite

import scala.meta.XtensionQuasiquoteTerm

class InvocationArgClassifierTest extends UnitTestSuite {

  private val ArgumentContextClassifications = Table(
    ("ArgumentContext", "ExpectedPassedByName"),
    (ArgumentContext(maybeParent = Some(q"Try.apply(x)"), index = 0), true),
    (ArgumentContext(maybeParent = Some(q"Try.apply[Int](x)"), index = 0), true),
    (ArgumentContext(maybeParent = Some(q"Future.apply(x)"), index = 0), true),
    (ArgumentContext(maybeParent = Some(q"Future.apply[Int](x)"), index = 0), true),
    (ArgumentContext(maybeParent = Some(q"Some(x)"), index = 0), false),
    (ArgumentContext(maybeParent = Some(q"Right(x)"), index = 0), false),
    (ArgumentContext(index = 0), false),
    (ArgumentContext(index = 1), false)
  )

  forAll(ArgumentContextClassifications) { case (argContext: ArgumentContext, expectedPassedByName: Boolean) =>
    test(s"$argContext should be considered as passed by ${if (expectedPassedByName) "name" else "value"}") {
      InvocationArgClassifier.isPassedByName(argContext)
    }
  }
}
