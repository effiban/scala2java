package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.classifiers.DefnVarClassifier
import io.github.effiban.scala2java.core.resolvers.JavaInheritanceKeywordResolver

object Enrichers {

  private[enrichers] lazy val defaultStatEnricher: DefaultStatEnricher = new DefaultStatEnricherImpl(
    DefnEnricher,
    DeclEnricher
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
}
