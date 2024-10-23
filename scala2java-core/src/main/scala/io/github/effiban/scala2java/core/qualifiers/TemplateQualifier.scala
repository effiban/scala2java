package io.github.effiban.scala2java.core.qualifiers

import scala.collection.immutable.ListMap
import scala.meta.{Init, Self, Stat, Template}

trait TemplateQualifier {
  def qualify(template: Template, context: QualificationContext = QualificationContext()): Template
}

private[qualifiers] class TemplateQualifierImpl(treeQualifier: => TreeQualifier) extends TemplateQualifier {

  override def qualify(template: Template, context: QualificationContext = QualificationContext()): Template = {
    // TODO - handle "early" definitions
    val qualifiedInitMap = ListMap.from(
      template.inits.map(init => (init, treeQualifier.qualify(init, context).asInstanceOf[Init]))
    )
    val qualifiedInitTypeMap = qualifiedInitMap.map { case (init, qualifiedInit) => (init.tpe, qualifiedInit.tpe)}

    val qualifiedSelf = treeQualifier.qualify(template.self, context).asInstanceOf[Self]
    val qualifiedSelfTypeMap = (template.self.decltpe, qualifiedSelf.decltpe) match {
      case (Some(tpe), Some(qualifiedType)) => Map(tpe -> qualifiedType)
      case _ => Map.empty
    }

    val qualifiedTypeMap = qualifiedInitTypeMap ++ qualifiedSelfTypeMap

    // TODO  - pass template imports to children
    val childContext = QualificationContext(importers = context.importers, qualifiedTypeMap = qualifiedTypeMap ++ context.qualifiedTypeMap)

    val qualifiedStats = template.stats.map(stat => treeQualifier.qualify(stat, childContext).asInstanceOf[Stat])

    template.copy(inits = qualifiedInitMap.values.toList, self = qualifiedSelf, stats = qualifiedStats)
  }
}
