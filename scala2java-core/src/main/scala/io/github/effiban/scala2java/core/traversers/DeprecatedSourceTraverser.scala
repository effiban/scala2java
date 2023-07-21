package io.github.effiban.scala2java.core.traversers

import scala.meta.Source

@deprecated
trait DeprecatedSourceTraverser extends ScalaTreeTraverser[Source]

@deprecated
private[traversers] class DeprecatedSourceTraverserImpl(statTraverser: => DeprecatedStatTraverser) extends DeprecatedSourceTraverser {

  // source file
  def traverse(source: Source): Unit = {
    source.stats.foreach(statTraverser.traverse(_))
  }
}
