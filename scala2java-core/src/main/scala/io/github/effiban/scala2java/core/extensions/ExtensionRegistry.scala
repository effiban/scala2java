package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.spi.predicates.{ImporterExcludedPredicate, TemplateInitExcludedPredicate}
import io.github.effiban.scala2java.spi.providers.AdditionalImportersProvider
import io.github.effiban.scala2java.spi.transformers.{ClassNameTransformer, DefnDefTransformer, TermApplyTypeToTermApplyTransformer}

case class ExtensionRegistry(extensions: List[Scala2JavaExtension]) {

  val additionalImportersProviders: List[AdditionalImportersProvider] = extensions.map(_.additionalImportersProvider())

  val importerExcludedPredicates: List[ImporterExcludedPredicate] = extensions.map(_.importerExcludedPredicate())

  val templateInitExcludedPredicates: List[TemplateInitExcludedPredicate] = extensions.map(_.templateInitExcludedPredicate())

  val classNameTransformers: List[ClassNameTransformer] = extensions.map(_.classNameTransformer())

  val defnDefTransformers: List[DefnDefTransformer] = extensions.map(_.defnDefTransformer())

  val termApplyTypeToTermApplyTransformers: List[TermApplyTypeToTermApplyTransformer] =
    extensions.map(_.termApplyTypeToTermApplyTransformer())
}
