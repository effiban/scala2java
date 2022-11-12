package io.github.effiban.scala2java.spi.predicates

import scala.meta.Init

trait TemplateInitExcludedPredicate extends (Init => Boolean)

object TemplateInitExcludedPredicate {
  val None: TemplateInitExcludedPredicate = _ => false
}