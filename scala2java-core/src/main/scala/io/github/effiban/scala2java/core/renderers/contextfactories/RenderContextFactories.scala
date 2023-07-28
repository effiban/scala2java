package io.github.effiban.scala2java.core.renderers.contextfactories

object RenderContextFactories {

  private[contextfactories] lazy val declRenderContextFactory: DeclRenderContextFactory = new DeclRenderContextFactoryImpl()

  private[contextfactories] lazy val defaultStatRenderContextFactory: DefaultStatRenderContextFactory =
    new DefaultStatRenderContextFactoryImpl(
      pkgRenderContextFactory,
      declRenderContextFactory,
      defnRenderContextFactory
    )

  private[contextfactories] lazy val defnRenderContextFactory: DefnRenderContextFactory = new DefnRenderContextFactoryImpl()

  private[contextfactories] lazy val pkgRenderContextFactory: PkgRenderContextFactory =
    new PkgRenderContextFactoryImpl(defaultStatRenderContextFactory)

  lazy val sourceRenderContextFactory: SourceRenderContextFactory = new SourceRenderContextFactoryImpl(defaultStatRenderContextFactory)

}
