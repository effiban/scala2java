package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.spi.predicates.TemplateInitExcludedPredicate

import scala.meta.Template

trait TemplateInitCleanup {

  def cleanup(template: Template): Template
}

private[cleanup] class TemplateInitCleanupImpl(templateInitExcludedPredicate: TemplateInitExcludedPredicate) extends TemplateInitCleanup {

  override def cleanup(template: Template): Template = {
    template.copy(inits = template.inits.filterNot(templateInitExcludedPredicate))
  }
}
