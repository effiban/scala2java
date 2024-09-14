package io.github.effiban.scala2java.core.qualifiers

import scala.meta.{Init, Self, Stat, Template}

trait TemplateQualifier {
  def qualify(template: Template, context: QualificationContext = QualificationContext()): Template
}

private[qualifiers] class TemplateQualifierImpl(treeQualifier: => TreeQualifier) extends TemplateQualifier {

  override def qualify(template: Template, context: QualificationContext = QualificationContext()): Template = {
    // TODO - handle "early" definitions
    val qualifiedInits = template.inits.map(init => treeQualifier.qualify(init, context).asInstanceOf[Init])
    val qualifiedSelf = treeQualifier.qualify(template.self, context).asInstanceOf[Self]

    val templateWithQualifiedParents = template.copy(inits = qualifiedInits, self = qualifiedSelf)

    // TODO  - pass template imports to children
    val childContext = QualificationContext(importers = context.importers)

    val qualifiedStats = templateWithQualifiedParents.stats.map(stat => treeQualifier.qualify(stat, childContext).asInstanceOf[Stat])
    templateWithQualifiedParents.copy(stats = qualifiedStats)
  }
}
