package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.collectors.TemplateAncestorsCollector
import io.github.effiban.scala2java.core.qualifiers.{QualificationContext, TemplateParentsByContextQualifier}

import scala.meta.{Template, Type}

trait TemplateAncestorsInferrer {
  def infer(template: Template, context: QualificationContext): List[Type.Ref]
}

private[typeinference] class TemplateAncestorsInferrerImpl(templateAncestorsCollector: TemplateAncestorsCollector,
                                                           templateParentsByContextQualifier: TemplateParentsByContextQualifier)
  extends TemplateAncestorsInferrer {

  override def infer(template: Template, context: QualificationContext): List[Type.Ref] = {
    val qualifiedTempl = templateParentsByContextQualifier.qualify(template, context)
    templateAncestorsCollector.collect(qualifiedTempl)
  }
}

object TemplateAncestorsInferrer extends TemplateAncestorsInferrerImpl(
  TemplateAncestorsCollector,
  TemplateParentsByContextQualifier
)