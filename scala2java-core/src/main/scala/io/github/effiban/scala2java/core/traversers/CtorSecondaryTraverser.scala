package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.spi.entities.JavaScope.MethodSignature

import scala.meta.{Ctor, Name}

trait CtorSecondaryTraverser {
  def traverse(secondaryCtor: Ctor.Secondary): Ctor.Secondary
}

private[traversers] class CtorSecondaryTraverserImpl(statModListTraverser: => StatModListTraverser,
                                                     termParamTraverser: => TermParamTraverser,
                                                     initTraverser: => InitTraverser,
                                                     blockStatTraverser: => BlockStatTraverser) extends CtorSecondaryTraverser {

  override def traverse(secondaryCtor: Ctor.Secondary): Ctor.Secondary = {
    val traversedMods = traverseMods(secondaryCtor)
    val traversedParams = traverseParams(secondaryCtor)
    val traversedInit = traverseInit(secondaryCtor)
    val traversedStats = traverseStats(secondaryCtor)

    Ctor.Secondary(
      mods = traversedMods,
      name = Name.Anonymous(),
      paramss = traversedParams,
      init = traversedInit,
      stats = traversedStats
    )
  }

  private def traverseMods(secondaryCtor: Ctor.Secondary) = {
    statModListTraverser.traverse(secondaryCtor.mods)
  }

  private def traverseParams(secondaryCtor: Ctor.Secondary) = {
    secondaryCtor.paramss.map(_.map(param => termParamTraverser.traverse(param, StatContext(MethodSignature))))
  }

  private def traverseInit(secondaryCtor: Ctor.Secondary) = {
    initTraverser.traverse(secondaryCtor.init)
  }


  private def traverseStats(secondaryCtor: Ctor.Secondary) = {
    secondaryCtor.stats.map(blockStatTraverser.traverse)
  }
}
