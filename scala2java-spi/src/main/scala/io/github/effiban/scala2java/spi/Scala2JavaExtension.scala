package io.github.effiban.scala2java.spi

import io.github.effiban.scala2java.spi.predicates.{ImporterExcludedPredicate, TemplateInitExcludedPredicate}
import io.github.effiban.scala2java.spi.providers.AdditionalImportersProvider
import io.github.effiban.scala2java.spi.transformers.{ClassNameTransformer, DefnDefTransformer, TermApplyTransformer, TermApplyTypeToTermApplyTransformer}

trait Scala2JavaExtension {

  def additionalImportersProvider(): AdditionalImportersProvider = AdditionalImportersProvider.Empty

  def importerExcludedPredicate(): ImporterExcludedPredicate = ImporterExcludedPredicate.None

  def templateInitExcludedPredicate(): TemplateInitExcludedPredicate = TemplateInitExcludedPredicate.None

  def classNameTransformer(): ClassNameTransformer = ClassNameTransformer.Identity

  def defnDefTransformer(): DefnDefTransformer = DefnDefTransformer.Identity

  def termApplyTypeToTermApplyTransformer(): TermApplyTypeToTermApplyTransformer = TermApplyTypeToTermApplyTransformer.Empty

  def termApplyTransformer(): TermApplyTransformer = TermApplyTransformer.Identity
}
