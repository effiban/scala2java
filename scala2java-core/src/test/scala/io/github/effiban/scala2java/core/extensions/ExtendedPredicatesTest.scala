package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.spi.predicates._

class ExtendedPredicatesTest extends UnitTestSuite {

  private val extension1 = mock[Scala2JavaExtension]
  private val extension2 = mock[Scala2JavaExtension]
  private val extensions = List(extension1, extension2)

  test("templateInitExcludedPredicates") {
    val templateInitExcludedPredicate1 = mock[TemplateInitExcludedPredicate]
    val templateInitExcludedPredicate2 = mock[TemplateInitExcludedPredicate]
    val templateInitExcludedPredicates = List(templateInitExcludedPredicate1, templateInitExcludedPredicate2)

    when(extension1.templateInitExcludedPredicate()).thenReturn(templateInitExcludedPredicate1)
    when(extension2.templateInitExcludedPredicate()).thenReturn(templateInitExcludedPredicate2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.templateInitExcludedPredicates shouldBe templateInitExcludedPredicates
  }

  test("termSelectHasApplyMethods") {
    val termSelectHasApplyMethod1 = mock[TermSelectHasApplyMethod]
    val termSelectHasApplyMethod2 = mock[TermSelectHasApplyMethod]
    val termSelectHasApplyMethods = List(termSelectHasApplyMethod1, termSelectHasApplyMethod2)

    when(extension1.termSelectHasApplyMethod()).thenReturn(termSelectHasApplyMethod1)
    when(extension2.termSelectHasApplyMethod()).thenReturn(termSelectHasApplyMethod2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.termSelectHasApplyMethods shouldBe termSelectHasApplyMethods
  }

  test("termSelectSupportsNoArgInvocations") {
    val termSelectSupportsNoArgInvocation1 = mock[TermSelectSupportsNoArgInvocation]
    val termSelectSupportsNoArgInvocation2 = mock[TermSelectSupportsNoArgInvocation]
    val termSelectSupportsNoArgInvocations = List(termSelectSupportsNoArgInvocation1, termSelectSupportsNoArgInvocation2)

    when(extension1.termSelectSupportsNoArgInvocation()).thenReturn(termSelectSupportsNoArgInvocation1)
    when(extension2.termSelectSupportsNoArgInvocation()).thenReturn(termSelectSupportsNoArgInvocation2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.termSelectSupportsNoArgInvocations shouldBe termSelectSupportsNoArgInvocations
  }
}
