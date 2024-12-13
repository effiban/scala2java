package io.github.effiban.scala2java.core.qualifiers

import io.github.effiban.scala2java.core.entities.TreeKeyedMap

import scala.meta.{Template, Type}

trait TemplateParentsByContextQualifier {
  def qualify(template: Template, context: QualificationContext): Template
}

object TemplateParentsByContextQualifier extends TemplateParentsByContextQualifier {

  def qualify(template: Template, context: QualificationContext): Template = {
    import template._

    val qualifiedInits = inits.map(init => init.copy(tpe = asQualified(init.tpe, context)))
    val qualifiedSelf = self.copy(decltpe = self.decltpe.map(t => asQualified(t, context)))
    template.copy(inits = qualifiedInits, self = qualifiedSelf)
  }

  private def asQualified(tpe: Type, context: QualificationContext) = TreeKeyedMap.get(context.qualifiedTypeMap, tpe).getOrElse(tpe)
}
