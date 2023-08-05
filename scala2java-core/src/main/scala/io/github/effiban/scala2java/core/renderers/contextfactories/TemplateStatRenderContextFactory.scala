package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedCtorSecondary, EnrichedEnumConstantList, EnrichedStat}
import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.{EnumConstantListRenderContext, TemplateStatRenderContext}

trait TemplateStatRenderContextFactory {

  def apply(enrichedStat: EnrichedStat): TemplateStatRenderContext
}

private[contextfactories] class TemplateStatRenderContextFactoryImpl(ctorSecondaryRenderContextFactory: CtorSecondaryRenderContextFactory,
                                                                     defaultStatRenderContextFactory: => DefaultStatRenderContextFactory)
  extends TemplateStatRenderContextFactory {

  override def apply(enrichedStat: EnrichedStat): TemplateStatRenderContext = enrichedStat match {
    case enrichedCtorSecondary: EnrichedCtorSecondary => ctorSecondaryRenderContextFactory(enrichedCtorSecondary)
    case _: EnrichedEnumConstantList => EnumConstantListRenderContext
    case anEnrichedStat => createWithDefaultFactory(anEnrichedStat)
  }

  private def createWithDefaultFactory(anEnrichedStat: EnrichedStat) = {
    defaultStatRenderContextFactory(anEnrichedStat, SealedHierarchies()) match {
      case renderContext: TemplateStatRenderContext => renderContext
      // TODO this shouldn't happen, but the render context hierarchy should be fixed to avoid this case altogether
      case renderContext => throw new IllegalStateException(
        s"The render context must extend TemplateStatRenderContext at this point but it is: $renderContext")
    }
  }
}
