package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TemplateContext
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.Term.NewAnonymous

trait NewAnonymousTraverser extends ScalaTreeTraverser1[NewAnonymous]

private[traversers] class NewAnonymousTraverserImpl(templateTraverser: => TemplateTraverser) extends NewAnonymousTraverser {

  // TODO - fully support the body by adding an enricher in the enrichment phase
  override def traverse(newAnonymous: NewAnonymous): NewAnonymous = {
    val traversedTemplate = templateTraverser.traverse(newAnonymous.templ, TemplateContext(JavaScope.Class))
    newAnonymous.copy(templ = traversedTemplate)
  }
}

