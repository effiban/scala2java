package io.github.effiban.scala2java.core.renderers.contextfactories

object RenderContextFactories {

  private[contextfactories] lazy val caseClassRenderContextFactory =
    new CaseClassRenderContextFactoryImpl(templateBodyRenderContextFactory)

  private[contextfactories] lazy val defaultStatRenderContextFactory: DefaultStatRenderContextFactory =
    new DefaultStatRenderContextFactoryImpl(
      pkgRenderContextFactory,
      DeclRenderContextFactory,
      defnRenderContextFactory
    )

  private[contextfactories] lazy val defnRenderContextFactory: DefnRenderContextFactory = new DefnRenderContextFactoryImpl(
    traitRenderContextFactory,
    caseClassRenderContextFactory,
    regularClassRenderContextFactory,
    objectRenderContextFactory
  )

  private[contextfactories] lazy val objectRenderContextFactory = new ObjectRenderContextFactoryImpl(templateBodyRenderContextFactory)

  private[contextfactories] lazy val pkgRenderContextFactory: PkgRenderContextFactory =
    new PkgRenderContextFactoryImpl(defaultStatRenderContextFactory)

  private[contextfactories] lazy val regularClassRenderContextFactory =
    new RegularClassRenderContextFactoryImpl(templateBodyRenderContextFactory)

  lazy val sourceRenderContextFactory: SourceRenderContextFactory = new SourceRenderContextFactoryImpl(defaultStatRenderContextFactory)

  private lazy val templateBodyRenderContextFactory: TemplateBodyRenderContextFactory =
     new TemplateBodyRenderContextFactoryImpl(templateStatRenderContextFactory)

  private lazy val templateStatRenderContextFactory: TemplateStatRenderContextFactory =
    new TemplateStatRenderContextFactoryImpl(CtorSecondaryRenderContextFactory, defaultStatRenderContextFactory)

  private[contextfactories] lazy val traitRenderContextFactory = new TraitRenderContextFactoryImpl(templateBodyRenderContextFactory)

}
