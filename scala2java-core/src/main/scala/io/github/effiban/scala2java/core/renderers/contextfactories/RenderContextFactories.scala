package io.github.effiban.scala2java.core.renderers.contextfactories

object RenderContextFactories {

  private[contextfactories] lazy val pkgRenderContextFactory: PkgRenderContextFactory =
    new PkgRenderContextFactoryImpl(statRenderContextFactory)

  lazy val sourceRenderContextFactory: SourceRenderContextFactory = new SourceRenderContextFactoryImpl(statRenderContextFactory)

  private[contextfactories] lazy val statRenderContextFactory: StatRenderContextFactory =
    new StatRenderContextFactoryImpl(pkgRenderContextFactory)
}
