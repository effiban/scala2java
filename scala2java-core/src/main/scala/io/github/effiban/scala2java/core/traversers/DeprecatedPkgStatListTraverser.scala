package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.resolvers.SealedHierarchiesResolver

import scala.meta.Stat

@deprecated
trait DeprecatedPkgStatListTraverser {
  def traverse(stats: List[Stat]): Unit
}

@deprecated
private[traversers] class DeprecatedPkgStatListTraverserImpl(pkgStatTraverser: => DeprecatedPkgStatTraverser,
                                                             sealedHierarchiesResolver: SealedHierarchiesResolver) extends DeprecatedPkgStatListTraverser {

  // TODO handle multiple definitions which are illegal in Java (such as 2 public classes in the same file)
  override def traverse(stats: List[Stat]): Unit = {
    val sealedHierarchies = sealedHierarchiesResolver.traverse(stats)
    stats.foreach(stat => pkgStatTraverser.traverse(stat, sealedHierarchies))
  }
}
