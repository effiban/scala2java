package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.reflection.ScalaReflectionClassifier

import scala.meta.{Template, Type}

/**
 * If a template extends an empty trait we can assume that this trait was created purposely
 * as a "marker" trait, and should always be considered as used,
 * even if there are no other references to it in the template.
 */
class IsTemplateAncestorUsedByEmptinessImpl(scalaReflectionClassifier: ScalaReflectionClassifier) extends IsTemplateAncestorUsed {

  def apply(template: Template, ancestorType: Type.Ref): Boolean =
    scalaReflectionClassifier.isNonTrivialEmptyType(ancestorType)
}

object IsTemplateAncestorUsedByEmptiness extends IsTemplateAncestorUsedByEmptinessImpl(ScalaReflectionClassifier)
