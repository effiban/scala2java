package io.github.effiban.scala2java.core.traversers

import scala.meta.Stat

trait PkgStatListTraverser {
  def traverse(stats: List[Stat]): List[Stat]
}

private[traversers] class PkgStatListTraverserImpl(pkgStatTraverser: => PkgStatTraverser) extends PkgStatListTraverser {

  override def traverse(stats: List[Stat]): List[Stat] = {
    stats.map(pkgStatTraverser.traverse)
      .collect { case Some(stat) => stat }
  }
}
