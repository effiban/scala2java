package io.github.effiban.scala2java.core.classifiers

import scala.meta.Template

trait TemplateClassifier {
  def isEnum(template: Template): Boolean
}

private[classifiers] class TemplateClassifierImpl(initClassifier: InitClassifier) extends TemplateClassifier {

  override def isEnum(template: Template): Boolean = template.inits.exists(initClassifier.isEnum)
}

object TemplateClassifier extends TemplateClassifierImpl(InitClassifier)