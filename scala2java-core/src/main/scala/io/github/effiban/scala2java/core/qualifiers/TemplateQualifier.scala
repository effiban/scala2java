package io.github.effiban.scala2java.core.qualifiers

import scala.meta.{Init, Self, Stat, Template}

trait TemplateQualifier {
  def qualify(template: Template, context: QualificationContext = QualificationContext()): Template
}

private[qualifiers] class TemplateQualifierImpl(treeQualifier: => TreeQualifier) extends TemplateQualifier {

  override def qualify(template: Template, context: QualificationContext): Template = {
    val qualifiedInits = template.inits.map(init => treeQualifier.qualify(init, context).asInstanceOf[Init])
    val qualifiedSelf = treeQualifier.qualify(template.self, context).asInstanceOf[Self]

    // TODO - collect parents and add to context for qualifying template stats

    val qualifiedStats = template.stats.map(stat => treeQualifier.qualify(stat, context).asInstanceOf[Stat])

    Template(
      // TODO qualify early definitions
      early = Nil,
      inits = qualifiedInits,
      self = qualifiedSelf,
      stats = qualifiedStats
    )
  }
}
