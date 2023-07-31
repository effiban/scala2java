package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TemplateContext
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.Term.NewAnonymous

trait NewAnonymousTraverser extends ScalaTreeTraverser1[NewAnonymous]

private[traversers] class NewAnonymousTraverserImpl(templateTraverser: => TemplateTraverser) extends NewAnonymousTraverser {

  override def traverse(newAnonymous: NewAnonymous): NewAnonymous = {
    val templateTraversalResult = templateTraverser.traverse(newAnonymous.templ, TemplateContext(JavaScope.Class))
    // TODO - fully support the body by returning the template stat results upwards
    newAnonymous.copy(templ = templateTraversalResult.template)
  }
}

