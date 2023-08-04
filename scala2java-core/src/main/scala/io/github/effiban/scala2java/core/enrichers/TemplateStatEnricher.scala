package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.classifiers.DefnVarClassifier
import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.enrichers.contexts.{CtorSecondaryEnrichmentContext, TemplateEnrichmentContext}
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedEnumConstantList, EnrichedStat}

import scala.meta.{Ctor, Defn, Stat, Type}

trait TemplateStatEnricher {
  def enrich(stat: Stat, context: TemplateEnrichmentContext = TemplateEnrichmentContext()): EnrichedStat
}

private[enrichers] class TemplateStatEnricherImpl(ctorSecondaryEnricher: CtorSecondaryEnricher,
                                                  defaultStatEnricher: => DefaultStatEnricher,
                                                  defnVarClassifier: DefnVarClassifier) extends TemplateStatEnricher {

  override def enrich(stat: Stat, context: TemplateEnrichmentContext = TemplateEnrichmentContext()): EnrichedStat = stat match {
    case secondaryCtor: Ctor.Secondary => enrichSecondaryCtor(secondaryCtor, context)
    case defnVar: Defn.Var if defnVarClassifier.isEnumConstantList(defnVar, context.javaScope) => EnrichedEnumConstantList(defnVar)
    case stat: Stat => enrichRegularStat(stat, context)
  }

  private def enrichSecondaryCtor(secondaryCtor: Ctor.Secondary, context: TemplateEnrichmentContext) = {
    context.maybeClassName match {
      case Some(className) => ctorSecondaryEnricher.enrich(secondaryCtor, toCtorContext(context, className))
      case None => throw new IllegalStateException("Stat is a secondary constructor but class name is missing")
    }
  }

  private def enrichRegularStat(stat: Stat, context: TemplateEnrichmentContext) = {
    defaultStatEnricher.enrich(stat, StatContext(context.javaScope))
  }

  private def toCtorContext(context: TemplateEnrichmentContext, className: Type.Name) = {
    CtorSecondaryEnrichmentContext(javaScope = context.javaScope, className = className)
  }
}
