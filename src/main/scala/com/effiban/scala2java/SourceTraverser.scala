package com.effiban.scala2java

import scala.meta.Source

trait SourceTraverser extends ScalaTreeTraverser[Source]

object SourceTraverser extends SourceTraverser {

  // source file
  def traverse(source: Source): Unit = {
    source.stats.foreach(StatTraverser.traverse)
  }
}
