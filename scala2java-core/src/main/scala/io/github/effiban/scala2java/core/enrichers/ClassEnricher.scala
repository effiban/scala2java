package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.classifiers.ClassClassifier
import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedClass

import scala.meta.Defn

trait ClassEnricher {
  def enrich(classDef: Defn.Class, context: StatContext = StatContext()): EnrichedClass
}

private[enrichers] class ClassEnricherImpl(caseClassEnricher: => CaseClassEnricher,
                                           regularClassEnricher: => RegularClassEnricher,
                                           classClassifier: ClassClassifier) extends ClassEnricher {

  def enrich(classDef: Defn.Class, context: StatContext = StatContext()): EnrichedClass = {
    if (classClassifier.isCase(classDef)) {
      caseClassEnricher.enrich(classDef, context)
    } else {
      regularClassEnricher.enrich(classDef, context)
    }
  }
}
