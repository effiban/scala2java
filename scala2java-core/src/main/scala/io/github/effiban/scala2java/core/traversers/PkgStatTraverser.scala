package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ClassOrTraitContext, StatContext}
import io.github.effiban.scala2java.core.traversers.results.{EmptyStatTraversalResult, PopulatedStatTraversalResult}
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.{Defn, Stat}

trait PkgStatTraverser {
  def traverse(stat: Stat): Option[Stat]
}

private[traversers] class PkgStatTraverserImpl(classTraverser: => ClassTraverser,
                                               traitTraverser: => TraitTraverser,
                                               objectTraverser: => ObjectTraverser,
                                               defaultStatTraverser: => DefaultStatTraverser) extends PkgStatTraverser {

  override def traverse(stat: Stat): Option[Stat] = {
    stat match {
      case `class`: Defn.Class => Some(classTraverser.traverse(`class`, ClassOrTraitContext(JavaScope.Package)).tree)
      case `trait`: Defn.Trait => Some(traitTraverser.traverse(`trait`, ClassOrTraitContext(JavaScope.Package)).tree)
      case `object`: Defn.Object => Some(objectTraverser.traverse(`object`, StatContext(JavaScope.Package)).tree)
      case stat => defaultStatTraverser.traverse(stat, StatContext(JavaScope.Package)) match {
        case populatedStatResult: PopulatedStatTraversalResult => Some(populatedStatResult.tree)
        case EmptyStatTraversalResult => None
      }
    }
  }
}
