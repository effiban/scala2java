package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.collectors.TemplateAncestorsCollector
import io.github.effiban.scala2java.core.entities.TreeKeyedMap
import io.github.effiban.scala2java.core.qualifiers.{QualificationContext, TemplateByContextQualifier}

import scala.collection.immutable.ListMap
import scala.meta.{Template, Tree, Type}

trait EnclosingTemplateAncestorsInferrer {
  def infer(tree: Tree, context: QualificationContext): ListMap[Template, List[Type.Ref]]
}

private[typeinference] class EnclosingTemplateAncestorsInferrerImpl(enclosingTemplatesInferrer: EnclosingTemplatesInferrer,
                                                                    templateAncestorsCollector: TemplateAncestorsCollector,
                                                                    templateByContextQualifier: TemplateByContextQualifier)
  extends EnclosingTemplateAncestorsInferrer {

  override def infer(tree: Tree, context: QualificationContext): ListMap[Template, List[Type.Ref]] = {
    ListMap.from(
      enclosingTemplatesInferrer.infer(tree)
        .map(template => (template, templateByContextQualifier.qualify(template, context)))
        .map { case (templ, qualifiedTempl) => (templ, templateAncestorsCollector.collect(qualifiedTempl)) }
        .filterNot { case (_, ancestors) => ancestors.isEmpty }
    )
  }
}

object EnclosingTemplateAncestorsInferrer extends EnclosingTemplateAncestorsInferrerImpl(
  EnclosingTemplatesInferrer,
  TemplateAncestorsCollector,
  TemplateByContextQualifier
)