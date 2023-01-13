package io.github.effiban.scala2java.core.collectors

import scala.meta.{Source, Tree}

trait SourceCollector[T <: Tree] {
  def collect(source: Source): List[T]
}
