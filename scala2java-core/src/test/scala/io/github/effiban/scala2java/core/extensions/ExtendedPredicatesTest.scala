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

  test("termNameHasApplyMethods") {
    val termNameHasApplyMethod1 = mock[TermNameHasApplyMethod]
    val termNameHasApplyMethod2 = mock[TermNameHasApplyMethod]
    val termNameHasApplyMethods = List(termNameHasApplyMethod1, termNameHasApplyMethod2)

    when(extension1.termNameHasApplyMethod()).thenReturn(termNameHasApplyMethod1)
    when(extension2.termNameHasApplyMethod()).thenReturn(termNameHasApplyMethod2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.termNameHasApplyMethods shouldBe termNameHasApplyMethods
  }

  test("termNameSupportsNoArgInvocations") {
    val termNameSupportsNoArgInvocation1 = mock[TermNameSupportsNoArgInvocation]
    val termNameSupportsNoArgInvocation2 = mock[TermNameSupportsNoArgInvocation]
    val termNameSupportsNoArgInvocations = List(termNameSupportsNoArgInvocation1, termNameSupportsNoArgInvocation2)

    when(extension1.termNameSupportsNoArgInvocation()).thenReturn(termNameSupportsNoArgInvocation1)
    when(extension2.termNameSupportsNoArgInvocation()).thenReturn(termNameSupportsNoArgInvocation2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.termNameSupportsNoArgInvocations shouldBe termNameSupportsNoArgInvocations
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
