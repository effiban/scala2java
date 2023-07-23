package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.traversers.results.{PopulatedStatTraversalResult, SourceTraversalResult}

import scala.meta.Source

trait SourceTraverser {
  def traverse(source: Source): SourceTraversalResult
}

private[traversers] class SourceTraverserImpl(defaultStatTraverser: => DefaultStatTraverser) extends SourceTraverser {

  // source file
  def traverse(source: Source): SourceTraversalResult = {
    val statResults = source.stats.map(stat => defaultStatTraverser.traverse(stat))
      .collect { case result: PopulatedStatTraversalResult => result }
    SourceTraversalResult(statResults)
  }
}
