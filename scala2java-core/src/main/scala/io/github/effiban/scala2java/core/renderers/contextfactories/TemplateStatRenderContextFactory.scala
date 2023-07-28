package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.{EnumConstantListRenderContext, TemplateStatRenderContext}
import io.github.effiban.scala2java.core.traversers.results.{CtorSecondaryTraversalResult, EnumConstantListTraversalResult, PopulatedStatTraversalResult}

trait TemplateStatRenderContextFactory {
  def apply(statTraversalResult: PopulatedStatTraversalResult): TemplateStatRenderContext
}

private[contextfactories] class TemplateStatRenderContextFactoryImpl(ctorSecondaryRenderContextFactory: CtorSecondaryRenderContextFactory,
                                                                     defaultStatRenderContextFactory: => DefaultStatRenderContextFactory)
  extends TemplateStatRenderContextFactory {

  override def apply(statTraversalResult: PopulatedStatTraversalResult): TemplateStatRenderContext = statTraversalResult match {
    case ctorSecondaryTraversalResult: CtorSecondaryTraversalResult => ctorSecondaryRenderContextFactory(ctorSecondaryTraversalResult)
    case _: EnumConstantListTraversalResult => EnumConstantListRenderContext
    case aStatTraversalResult => createWithDefaultFactory(aStatTraversalResult)
  }

  private def createWithDefaultFactory(aStatTraversalResult: PopulatedStatTraversalResult) = {
    defaultStatRenderContextFactory(aStatTraversalResult) match {
      case renderContext: TemplateStatRenderContext => renderContext
      // TODO this shouldn't happen, but the render context hierarchy should be fixed to avoid this case altogether
      case renderContext => throw new IllegalStateException(
        s"The render context must extend TemplateStatRenderContext at this point but it is: $renderContext")
    }
  }
}
