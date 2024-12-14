package io.github.effiban.scala2java.core.unqualifiers

import io.github.effiban.scala2java.core.qualifiers.QualificationContext

import scala.meta.{Init, Self, Stat, Template}

trait TemplateUnqualifier {
  def unqualify(template: Template, context: QualificationContext = QualificationContext()): Template
}

private[unqualifiers] class TemplateUnqualifierImpl(treeUnqualifier: => TreeUnqualifier) extends TemplateUnqualifier {

  override def unqualify(template: Template, context: QualificationContext = QualificationContext()): Template = {

    // The template children must be qualified before the template so that the inits (parents) will still be qualified when
    // the children need to examine them (for example 'super.x()' needs to examine the parents to do the unqualification properly,
    // since they are Java parents that are not (necessarily) the compiled Scala parents of the class, and cannot be obtained by reflection).

    // TODO - handle "early" definitions
    val unqualifiedInits = template.inits.map(init => treeUnqualifier.unqualify(init, context).asInstanceOf[Init])
    val unqualifiedSelf = treeUnqualifier.unqualify(template.self, context).asInstanceOf[Self]

    val parentUnqualifiedTemplate = template.copy(inits = unqualifiedInits, self = unqualifiedSelf)

    val unqualifiedStats = template.stats.map(stat => treeUnqualifier.unqualify(stat, context).asInstanceOf[Stat])
    parentUnqualifiedTemplate.copy(stats = unqualifiedStats)
  }
}
