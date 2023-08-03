package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedDecl, EnrichedUnsupportedDecl}

import scala.meta.Decl

trait DeclEnricher {
  def enrich(decl: Decl, context: StatContext = StatContext()): EnrichedDecl

}

private[enrichers] class DeclEnricherImpl(declVarEnricher: DeclVarEnricher,
                                          declDefEnricher: DeclDefEnricher) extends DeclEnricher {

  override def enrich(decl: Decl, context: StatContext = StatContext()): EnrichedDecl = decl match {
    case varDecl: Decl.Var => declVarEnricher.enrich(varDecl, context)
    case defDecl: Decl.Def => declDefEnricher.enrich(defDecl, context)
    case decl => EnrichedUnsupportedDecl(decl)
  }
}

object DeclEnricher extends DeclEnricherImpl(DeclVarEnricher, DeclDefEnricher)