package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.Scala2JavaExtension
import io.github.effiban.scala2java.spi.predicates.{ImporterExcludedPredicate, TemplateInitExcludedPredicate}
import io.github.effiban.scala2java.spi.providers.AdditionalImportersProvider
import io.github.effiban.scala2java.spi.transformers._

case class ExtensionRegistry(extensions: List[Scala2JavaExtension] = Nil) {

  val fileNameTransformers: List[FileNameTransformer] = extensions.map(_.fileNameTransformer())

  val additionalImportersProviders: List[AdditionalImportersProvider] = extensions.map(_.additionalImportersProvider())

  val importerExcludedPredicates: List[ImporterExcludedPredicate] = extensions.map(_.importerExcludedPredicate())

  val templateInitExcludedPredicates: List[TemplateInitExcludedPredicate] = extensions.map(_.templateInitExcludedPredicate())

  val classTransformers: List[ClassTransformer] = extensions.map(_.classTransformer())

  val defnValTransformers: List[DefnValTransformer] = extensions.map(_.defnValTransformer())

  val defnValToDeclVarTransformers: List[DefnValToDeclVarTransformer] = extensions.map(_.defnValToDeclVarTransformer())

  val defnDefTransformers: List[DefnDefTransformer] = extensions.map(_.defnDefTransformer())

  val termApplyTypeToTermApplyTransformers: List[TermApplyTypeToTermApplyTransformer] =
    extensions.map(_.termApplyTypeToTermApplyTransformer())

  val termApplyTransformers: List[TermApplyTransformer] = extensions.map(_.termApplyTransformer())

  val termSelectTransformers: List[TermSelectTransformer] = extensions.map(_.termSelectTransformer())
}
