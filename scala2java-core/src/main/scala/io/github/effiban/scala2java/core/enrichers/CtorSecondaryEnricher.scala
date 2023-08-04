package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.enrichers.contexts.CtorSecondaryEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedCtorSecondary
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver

import scala.meta.Ctor

trait CtorSecondaryEnricher {
  def enrich(secondaryCtor: Ctor.Secondary, ctorContext: CtorSecondaryEnrichmentContext): EnrichedCtorSecondary
}

private[enrichers] class CtorSecondaryEnricherImpl(javaModifiersResolver: JavaModifiersResolver) extends CtorSecondaryEnricher {

  override def enrich(secondaryCtor: Ctor.Secondary, context: CtorSecondaryEnrichmentContext): EnrichedCtorSecondary = {
    val javaModifiers = javaModifiersResolver.resolve(ModifiersContext(secondaryCtor, JavaTreeType.Method, context.javaScope))
    EnrichedCtorSecondary(
      stat = secondaryCtor,
      className = context.className,
      javaModifiers = javaModifiers
    )
  }
}
