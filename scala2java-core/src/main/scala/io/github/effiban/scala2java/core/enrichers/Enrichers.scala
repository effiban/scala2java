package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.classifiers.DefnVarClassifier
import io.github.effiban.scala2java.core.resolvers.{JavaInheritanceKeywordResolver, JavaModifiersResolver}

object Enrichers {

  private[enrichers] lazy val defaultStatEnricher: DefaultStatEnricher = new DefaultStatEnricherImpl(
    defnEnricher,
    DeclEnricher
  )

  private[enrichers] lazy val defnEnricher: DefnEnricher = new DefnEnricherImpl(
    DefnVarEnricher,
    DefnDefEnricher,
    traitEnricher
  )

  private[enrichers] lazy val templateBodyEnricher: TemplateBodyEnricher = new TemplateBodyEnricherImpl(templateStatEnricher)

  private[enrichers] lazy val templateEnricher: TemplateEnricher = new TemplateEnricherImpl(
    templateBodyEnricher,
    JavaInheritanceKeywordResolver
  )

  private[enrichers] lazy val templateStatEnricher: TemplateStatEnricher = new TemplateStatEnricherImpl(
    CtorSecondaryEnricher,
    defaultStatEnricher,
    DefnVarClassifier
  )

  private[enrichers] lazy val traitEnricher: TraitEnricher = new TraitEnricherImpl(templateEnricher, JavaModifiersResolver)
}
