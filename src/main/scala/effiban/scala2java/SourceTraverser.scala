package effiban.scala2java

import scala.meta.Source

trait SourceTraverser extends ScalaTreeTraverser[Source]

private[scala2java] class SourceTraverserImpl(statTraverser: => StatTraverser) extends SourceTraverser {

  // source file
  def traverse(source: Source): Unit = {
    source.stats.foreach(statTraverser.traverse)
  }
}

object SourceTraverser extends SourceTraverserImpl(StatTraverser)
