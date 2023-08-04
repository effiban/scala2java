package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.classifiers.{ClassClassifier, DefnVarClassifier}
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaInheritanceKeywordResolver, JavaModifiersResolver, JavaTreeTypeResolver}

object Enrichers {

  private[enrichers] lazy val caseClassEnricher: CaseClassEnricher = new CaseClassEnricherImpl(
    templateEnricher,
    JavaModifiersResolver,
    JavaChildScopeResolver
  )

  private[enrichers] lazy val classEnricher: ClassEnricher = new ClassEnricherImpl(
    caseClassEnricher,
    regularClassEnricher,
    ClassClassifier
  )

  private[enrichers] lazy val defaultStatEnricher: DefaultStatEnricher = new DefaultStatEnricherImpl(
    defnEnricher,
    DeclEnricher
  )

  private[enrichers] lazy val defnEnricher: DefnEnricher = new DefnEnricherImpl(
    DefnVarEnricher,
    DefnDefEnricher,
    traitEnricher,
    classEnricher,
    objectEnricher
  )

  private[enrichers] lazy val objectEnricher: ObjectEnricher = new ObjectEnricherImpl(
    templateEnricher,
    JavaModifiersResolver,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
  )

  private[enrichers] lazy val regularClassEnricher: RegularClassEnricher = new RegularClassEnricherImpl(
    templateEnricher,
    JavaModifiersResolver,
    JavaTreeTypeResolver,
    JavaChildScopeResolver
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
