package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.resolvers.SealedHierarchiesResolver
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Stat

trait PkgStatListTraverser {
  def traverse(stats: List[Stat]): Unit
}

private[traversers] class PkgStatListTraverserImpl(pkgStatTraverser: => PkgStatTraverser,
                                                   sealedHierarchiesResolver: SealedHierarchiesResolver)
                                                  (implicit javaWriter: JavaWriter) extends PkgStatListTraverser {

  // TODO handle multiple definitions which are illegal in Java (such as 2 public classes in the same file)
  override def traverse(stats: List[Stat]): Unit = {
    val sealedHierarchies = sealedHierarchiesResolver.traverse(stats)
    stats.foreach(stat => pkgStatTraverser.traverse(stat, sealedHierarchies))
  }
}
