package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.InvocationArgCoordinates

import scala.meta.XtensionQuasiquoteTerm

class CoreInvocationArgByNamePredicateTest extends UnitTestSuite {

  private val InvocationArgByNameClassifications = Table(
    ("InvocationArgCoordinates", "ExpectedPassedByName"),
    (InvocationArgCoordinates(invocation = q"Try.apply(x)", index = 0), true),
    (InvocationArgCoordinates(invocation = q"Try.apply[Int](x)", index = 0), true),
    (InvocationArgCoordinates(invocation = q"Try.ofSupplier(x)", index = 0), true),
    (InvocationArgCoordinates(invocation = q"Try.ofSupplier[Int](x)", index = 0), true),
    (InvocationArgCoordinates(invocation = q"Future.apply(x)", index = 0), true),
    (InvocationArgCoordinates(invocation = q"Future.apply[Int](x)", index = 0), true),
    (InvocationArgCoordinates(invocation = q"CompletableFuture.supplyAsync(x)", index = 0), true),
    (InvocationArgCoordinates(invocation = q"CompletableFuture.supplyAsync[Int](x)", index = 0), true),
    (InvocationArgCoordinates(invocation = q"Some(x)", maybeName = Some(q"value"), index = 0), false),
    (InvocationArgCoordinates(invocation = q"Some(x)", index = 0), false),
    (InvocationArgCoordinates(invocation = q"Right(x)", index = 0), false),
    (InvocationArgCoordinates(invocation = q"foo(x)", index = 0), false),
    (InvocationArgCoordinates(invocation = q"foo(x)", index = 1), false)
  )

  forAll(InvocationArgByNameClassifications) { case (argCoords: InvocationArgCoordinates, expectedPassedByName: Boolean) =>
    test(s"The argument at: $argCoords is passed by ${if (expectedPassedByName) "name" else "value"}") {
      CoreInvocationArgByNamePredicate(argCoords)
    }
  }
}
