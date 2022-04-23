package com.effiban.scala2java

import scala.meta.Source

object SourceTraverser extends ScalaTreeTraverser[Source] {

  def traverse(source: Source): Unit = {
    source.stats.foreach(GenericTreeTraverser.traverse)
  }
}
