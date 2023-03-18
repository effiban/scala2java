package io.github.effiban.scala2java.core.contexts

import scala.meta.Type

case class TermSelectInferenceContext(maybeQualType: Option[Type] = None)
