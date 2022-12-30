package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.spi.predicates.{ImporterExcludedPredicate, TemplateInitExcludedPredicate}

class ExtendedPredicatesTest extends UnitTestSuite {

  private val extension1 = mock[Scala2JavaExtension]
  private val extension2 = mock[Scala2JavaExtension]
  private val extensions = List(extension1, extension2)

  test("importerExcludedPredicates") {
    val importerExcludedPredicate1 = mock[ImporterExcludedPredicate]
    val importerExcludedPredicate2 = mock[ImporterExcludedPredicate]
    val importerExcludedPredicates = List(importerExcludedPredicate1, importerExcludedPredicate2)

    when(extension1.importerExcludedPredicate()).thenReturn(importerExcludedPredicate1)
    when(extension2.importerExcludedPredicate()).thenReturn(importerExcludedPredicate2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.importerExcludedPredicates shouldBe importerExcludedPredicates
  }

  test("templateInitExcludedPredicates") {
    val templateInitExcludedPredicate1 = mock[TemplateInitExcludedPredicate]
    val templateInitExcludedPredicate2 = mock[TemplateInitExcludedPredicate]
    val templateInitExcludedPredicates = List(templateInitExcludedPredicate1, templateInitExcludedPredicate2)

    when(extension1.templateInitExcludedPredicate()).thenReturn(templateInitExcludedPredicate1)
    when(extension2.templateInitExcludedPredicate()).thenReturn(templateInitExcludedPredicate2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.templateInitExcludedPredicates shouldBe templateInitExcludedPredicates
  }
}
