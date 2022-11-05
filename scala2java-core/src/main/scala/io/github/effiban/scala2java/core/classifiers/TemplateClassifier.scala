package io.github.effiban.scala2java.core.classifiers

import scala.meta.{Template, Type}

trait TemplateClassifier {
  def isEnum(template: Template): Boolean
}

object TemplateClassifier extends TemplateClassifier {

  override def isEnum(template: Template): Boolean = {
    template.inits
      .map(_.tpe)
      .collect { case typeName@Type.Name("Enumeration") => typeName }
      .nonEmpty
  }
}
