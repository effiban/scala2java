package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.spi.predicates.{ImporterExcludedPredicate, TemplateInitExcludedPredicate}
import io.github.effiban.scala2java.spi.providers.AdditionalImportersProvider
import io.github.effiban.scala2java.spi.transformers.{ClassNameTransformer, DefnDefTransformer, TermApplyTypeToTermApplyTransformer}

class ExtensionRegistryTest extends UnitTestSuite {

  private val extension1 = mock[Scala2JavaExtension]
  private val extension2 = mock[Scala2JavaExtension]
  private val extensions = List(extension1, extension2)

  test("additionalImportersProviders") {
    val additionalImportersProvider1 = mock[AdditionalImportersProvider]
    val additionalImportersProvider2 = mock[AdditionalImportersProvider]
    val additionalImportersProviders = List(additionalImportersProvider1, additionalImportersProvider2)

    when(extension1.additionalImportersProvider()).thenReturn(additionalImportersProvider1)
    when(extension2.additionalImportersProvider()).thenReturn(additionalImportersProvider2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.additionalImportersProviders shouldBe additionalImportersProviders
  }

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

  test("classNameTransformers") {
    val classNameTransformer1 = mock[ClassNameTransformer]
    val classNameTransformer2 = mock[ClassNameTransformer]
    val classNameTransformers = List(classNameTransformer1, classNameTransformer2)

    when(extension1.classNameTransformer()).thenReturn(classNameTransformer1)
    when(extension2.classNameTransformer()).thenReturn(classNameTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.classNameTransformers shouldBe classNameTransformers
  }

  test("defnDefTransformers") {
    val defnDefTransformer1 = mock[DefnDefTransformer]
    val defnDefTransformer2 = mock[DefnDefTransformer]
    val defnDefTransformers = List(defnDefTransformer1, defnDefTransformer2)

    when(extension1.defnDefTransformer()).thenReturn(defnDefTransformer1)
    when(extension2.defnDefTransformer()).thenReturn(defnDefTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.defnDefTransformers shouldBe defnDefTransformers
  }

  test("termApplyTypeToTermApplyTransformers") {
    val termApplyTypeToTermApplyTransformer1 = mock[TermApplyTypeToTermApplyTransformer]
    val termApplyTypeToTermApplyTransformer2 = mock[TermApplyTypeToTermApplyTransformer]
    val termApplyTypeToTermApplyTransformers = List(termApplyTypeToTermApplyTransformer1, termApplyTypeToTermApplyTransformer2)

    when(extension1.termApplyTypeToTermApplyTransformer()).thenReturn(termApplyTypeToTermApplyTransformer1)
    when(extension2.termApplyTypeToTermApplyTransformer()).thenReturn(termApplyTypeToTermApplyTransformer2)

    val extensionRegistry = ExtensionRegistry(extensions)

    extensionRegistry.termApplyTypeToTermApplyTransformers shouldBe termApplyTypeToTermApplyTransformers
  }
}
