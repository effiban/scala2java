package io.github.effiban.scala2java.core.traversers

import scala.meta.Source

trait SourceTraverser extends ScalaTreeTraverser[Source]

private[traversers] class SourceTraverserImpl(statTraverser: => StatTraverser) extends SourceTraverser {

  // source file
  def traverse(source: Source): Unit = {
    source.stats.foreach(statTraverser.traverse(_))
  }
}
