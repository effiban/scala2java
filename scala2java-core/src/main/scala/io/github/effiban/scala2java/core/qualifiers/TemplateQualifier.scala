package io.github.effiban.scala2java.core.qualifiers

import scala.meta.Term.NewAnonymous
import scala.meta.{Defn, Init, Self, Stat, Template, Tree}

trait TemplateQualifier {
  def qualify(template: Template, context: QualificationContext = QualificationContext()): Template
}

private[qualifiers] class TemplateQualifierImpl(treeQualifier: => TreeQualifier) extends TemplateQualifier {

  override def qualify(template: Template, context: QualificationContext = QualificationContext()): Template = {
    // TODO - handle "early" definitions
    val qualifiedInits = template.inits.map(init => treeQualifier.qualify(init, context).asInstanceOf[Init])
    val qualifiedSelf = treeQualifier.qualify(template.self, context).asInstanceOf[Self]

    val templateWithQualifiedJavaParents = linkToParent(template.parent, template.copy(inits = qualifiedInits, self = qualifiedSelf))

    // TODO  - pass template imports to children
    val childContext = QualificationContext(importers = context.importers)

    val qualifiedStats = templateWithQualifiedJavaParents.stats.map(stat => treeQualifier.qualify(stat, childContext).asInstanceOf[Stat])
    linkToParent(templateWithQualifiedJavaParents.parent, templateWithQualifiedJavaParents.copy(stats = qualifiedStats))
  }

  private def linkToParent(maybeOrigTemplateParent: Option[Tree], templateWithQualifiedInitsAndSelf: Template) = {
    maybeOrigTemplateParent match {
      case Some(aClass: Defn.Class) => aClass.copy(templ = templateWithQualifiedInitsAndSelf).templ
      case Some(aTrait: Defn.Trait) => aTrait.copy(templ = templateWithQualifiedInitsAndSelf).templ
      case Some(anObject: Defn.Object) => anObject.copy(templ = templateWithQualifiedInitsAndSelf).templ
      case Some(newAnon: NewAnonymous) => newAnon.copy(templ = templateWithQualifiedInitsAndSelf).templ
      case _ => templateWithQualifiedInitsAndSelf
    }
  }
}
