package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.extractors.InitTypeRefExtractor

import scala.meta.Template

trait TemplateInitCleanup {

  def cleanup(template: Template): Template
}

private[cleanup] class TemplateInitCleanupImpl(templateParentsUsedResolver: TemplateParentsUsedResolver)
  extends TemplateInitCleanup {

  override def cleanup(template: Template): Template = {
    val initTypesUsed = templateParentsUsedResolver.resolve(template)

    val initsUsed = template.inits
      .map(init => (init, InitTypeRefExtractor.extract(init)))
      .collect { case (init, Some(initType)) => (init, initType) }
      .filter { case (_, initType) => initTypesUsed.exists(_.structure == initType.structure) }
      .map { case (init, _) => init }

    template.copy(inits = initsUsed)
  }
}

