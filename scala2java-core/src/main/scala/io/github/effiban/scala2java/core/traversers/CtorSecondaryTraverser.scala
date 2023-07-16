package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.traversers.results.CtorSecondaryTraversalResult
import io.github.effiban.scala2java.spi.entities.JavaScope.MethodSignature

import scala.meta.{Ctor, Name}

trait CtorSecondaryTraverser {
  def traverse(secondaryCtor: Ctor.Secondary, ctorContext: CtorContext): CtorSecondaryTraversalResult
}

private[traversers] class CtorSecondaryTraverserImpl(statModListTraverser: => StatModListTraverser,
                                                     typeNameTraverser: => TypeNameTraverser,
                                                     termParamTraverser: => TermParamTraverser,
                                                     initTraverser: => InitTraverser,
                                                     blockStatTraverser: => BlockStatTraverser) extends CtorSecondaryTraverser {

  override def traverse(secondaryCtor: Ctor.Secondary, context: CtorContext): CtorSecondaryTraversalResult = {
    val modsTraversalResult = traverseMods(secondaryCtor, context)
    val traversedClassName = traverseClassName(context)
    val traversedParams = traverseParams(secondaryCtor)
    val traversedInit = traverseInit(secondaryCtor)
    val traversedStats = traverseStats(secondaryCtor)

    val traversedCtorSecondary = Ctor.Secondary(
      mods = modsTraversalResult.scalaMods,
      name = Name.Anonymous(),
      paramss = traversedParams,
      init = traversedInit,
      stats = traversedStats
    )

    CtorSecondaryTraversalResult(
      tree = traversedCtorSecondary,
      className = traversedClassName,
      javaModifiers = modsTraversalResult.javaModifiers
    )
  }

  private def traverseMods(secondaryCtor: Ctor.Secondary, context: CtorContext) = {
    statModListTraverser.traverse(ModifiersContext(secondaryCtor, JavaTreeType.Method, context.javaScope))
  }

  private def traverseClassName(context: CtorContext) = {
    typeNameTraverser.traverse(context.className)
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
