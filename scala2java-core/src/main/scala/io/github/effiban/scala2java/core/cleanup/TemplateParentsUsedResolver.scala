package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.collectors.TemplateAncestorsCollector

import scala.meta.{Template, Type}

trait TemplateParentsUsedResolver {
  def resolve(template: Template): List[Type.Ref]
}

private[cleanup] class TemplateParentsUsedResolverImpl(templateAncestorsCollector: TemplateAncestorsCollector,
                                                       isTemplateAncestorUsed: IsTemplateAncestorUsed)
  extends TemplateParentsUsedResolver {

  def resolve(template: Template): List[Type.Ref] = {
    val qualifiedParentsUsed =
      templateAncestorsCollector.collectToMap(template)
        .filter { case (_, ancestors) => ancestors.exists(ancestor => isTemplateAncestorUsed(template, ancestor)) }
        .keys
        .toList

    // TODO check the local parents more precisely. Now assuming that any parent which is unqualified
    //      at this point must be locally defined
    val localParents = template.inits.map(_.tpe).collect { case typeName : Type.Name => typeName }

    qualifiedParentsUsed ++ localParents
  }
}

object TemplateParentsUsedResolver extends TemplateParentsUsedResolverImpl(
  TemplateAncestorsCollector,
  CompositeIsTemplateAncestorUsed
)