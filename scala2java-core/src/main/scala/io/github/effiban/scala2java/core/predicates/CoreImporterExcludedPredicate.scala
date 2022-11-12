package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.classifiers.ImporterClassifier
import io.github.effiban.scala2java.spi.predicates.ImporterExcludedPredicate

import scala.meta.Importer

private[predicates] class CoreImporterExcludedPredicate(importerClassifier: ImporterClassifier) extends ImporterExcludedPredicate {

  // TODO instead of filtering out all scala imports, some can be converted to Java equivalents
  override def apply(importer: Importer): Boolean = importerClassifier.isScala(importer)
}

object CoreImporterExcludedPredicate extends CoreImporterExcludedPredicate(ImporterClassifier)
