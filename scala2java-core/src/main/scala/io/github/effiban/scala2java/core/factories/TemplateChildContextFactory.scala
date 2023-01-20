package io.github.effiban.scala2java.core.factories

import io.github.effiban.scala2java.core.contexts.{TemplateBodyContext, TemplateChildContext}

import scala.meta.Term

trait TemplateChildContextFactory {

  def create(context: TemplateBodyContext, terms: List[Term]): TemplateChildContext
}

object TemplateChildContextFactory extends TemplateChildContextFactory {

  override def create(context: TemplateBodyContext, terms: List[Term]): TemplateChildContext = {
    val ctorTerms = context.maybePrimaryCtor match {
      case Some(_) => terms
      case None => Nil
    }

    TemplateChildContext(
      javaScope = context.javaScope,
      maybeClassName = context.maybeClassName,
      inits = context.inits,
      ctorTerms = ctorTerms
    )
  }
}
