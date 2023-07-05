package io.github.effiban.scala2java.core.renderers.contextfactories

object RenderContextFactories {

  lazy val blockRenderContextFactory: BlockRenderContextFactory = new BlockRenderContextFactoryImpl(
    blockStatRenderContextFactory
  )

  private lazy val blockStatRenderContextFactory: BlockStatRenderContextFactory = new BlockStatRenderContextFactoryImpl(
    ifRenderContextFactory,
    tryRenderContextFactory
  )

  private lazy val ifRenderContextFactory: IfRenderContextFactory = new IfRenderContextFactoryImpl(
    blockRenderContextFactory
  )

  private lazy val tryRenderContextFactory: TryRenderContextFactory = new TryRenderContextFactoryImpl(
    blockRenderContextFactory
  )
}
