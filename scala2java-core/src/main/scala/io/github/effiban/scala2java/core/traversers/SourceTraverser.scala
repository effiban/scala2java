package io.github.effiban.scala2java.core.traversers

import scala.meta.{Source, Stat}

trait SourceTraverser {
  def traverse(source: Source): Source
}

private[traversers] class SourceTraverserImpl(defaultStatTraverser: => DefaultStatTraverser) extends SourceTraverser {

  // source file
  def traverse(source: Source): Source = {
    val traversedStats = source.stats.map(stat => defaultStatTraverser.traverse(stat))
      .collect { case Some(stat: Stat) => stat }
    Source(traversedStats)
  }
}
