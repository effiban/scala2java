package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.traversers.results.PopulatedStatTraversalResult

import scala.meta.Source

trait SourceTraverser {
  def traverse(source: Source): Source
}

private[traversers] class SourceTraverserImpl(defaultStatTraverser: => DefaultStatTraverser) extends SourceTraverser {

  // source file
  def traverse(source: Source): Source = {
    val statResults = source.stats.map(stat => defaultStatTraverser.traverse(stat))
      .collect { case result: PopulatedStatTraversalResult => result }
    Source(statResults.map(_.tree))
  }
}
