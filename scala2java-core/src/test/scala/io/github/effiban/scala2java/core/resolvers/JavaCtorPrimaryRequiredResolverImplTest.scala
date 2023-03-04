package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.classifiers.CtorPrimaryClassifier
import io.github.effiban.scala2java.core.contexts.CtorRequiredResolutionContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.PrimaryCtors
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.{Ctor, Name, XtensionQuasiquoteInit, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam}

class JavaCtorPrimaryRequiredResolverImplTest extends UnitTestSuite {

  private val nonDefaultCtorPrimary = Ctor.Primary(Nil, Name.Anonymous(), List(List(param"x: Int")))

  private val ctorPrimaryClassifier = mock[CtorPrimaryClassifier]

  private val ctorPrimaryRequiredResolver = new JavaCtorPrimaryRequiredResolverImpl(ctorPrimaryClassifier)

  test("isRequired() when ctor is non-default and other criteria false, should return true") {
    when(ctorPrimaryClassifier.isDefault(eqTree(nonDefaultCtorPrimary))).thenReturn(false)

    ctorPrimaryRequiredResolver.isRequired(nonDefaultCtorPrimary, CtorRequiredResolutionContext()) shouldBe true
  }

  test("isRequired() when init has params and other criteria false, should return true") {
    when(ctorPrimaryClassifier.isDefault(eqTree(PrimaryCtors.Empty))).thenReturn(true)

    val context = CtorRequiredResolutionContext(inits = List(init"Parent(x: Int)"))

    ctorPrimaryRequiredResolver.isRequired(PrimaryCtors.Empty, context) shouldBe true
  }

  test("isRequired() when there are terms and other criteria false, should return true") {
    when(ctorPrimaryClassifier.isDefault(eqTree(PrimaryCtors.Empty))).thenReturn(true)

    val context = CtorRequiredResolutionContext(terms = List(q"foo"))

    ctorPrimaryRequiredResolver.isRequired(PrimaryCtors.Empty, context) shouldBe true
  }

  test("isRequired() when there are secondary constructors and other criteria false, should return true") {
    when(ctorPrimaryClassifier.isDefault(eqTree(PrimaryCtors.Empty))).thenReturn(true)

    val context = CtorRequiredResolutionContext(otherStats =
      List(
        Ctor.Secondary(
          Nil,
          Name.Anonymous(),
          List(List(param"x: Int", param"y: String")),
          init"this(3)",
          Nil
        )
      )
    )

    ctorPrimaryRequiredResolver.isRequired(PrimaryCtors.Empty, context) shouldBe true
  }

  test("isRequired() when all criteria are false, should return false") {
    when(ctorPrimaryClassifier.isDefault(eqTree(PrimaryCtors.Empty))).thenReturn(true)

    ctorPrimaryRequiredResolver.isRequired(PrimaryCtors.Empty, CtorRequiredResolutionContext()) shouldBe false
  }
}
