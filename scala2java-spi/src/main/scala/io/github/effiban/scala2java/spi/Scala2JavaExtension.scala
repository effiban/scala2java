package io.github.effiban.scala2java.spi

import io.github.effiban.scala2java.spi.predicates.{ImporterExcludedPredicate, TemplateInitExcludedPredicate}
import io.github.effiban.scala2java.spi.providers.AdditionalImportersProvider
import io.github.effiban.scala2java.spi.transformers._

trait Scala2JavaExtension {

  def fileNameTransformer(): FileNameTransformer = FileNameTransformer.Identity

  def additionalImportersProvider(): AdditionalImportersProvider = AdditionalImportersProvider.Empty

  def importerExcludedPredicate(): ImporterExcludedPredicate = ImporterExcludedPredicate.None

  def classTransformer(): ClassTransformer = ClassTransformer.Identity

  def templateInitExcludedPredicate(): TemplateInitExcludedPredicate = TemplateInitExcludedPredicate.None

  def defnDefTransformer(): DefnDefTransformer = DefnDefTransformer.Identity

  def termApplyTypeToTermApplyTransformer(): TermApplyTypeToTermApplyTransformer = TermApplyTypeToTermApplyTransformer.Empty

  def termApplyTransformer(): TermApplyTransformer = TermApplyTransformer.Identity
}
