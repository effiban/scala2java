package io.github.effiban.scala2java.core.resolvers

import io.github.effiban.scala2java.core.classifiers.CtorPrimaryClassifier
import io.github.effiban.scala2java.core.contexts.CtorRequiredResolutionContext

import scala.meta.{Ctor, Init, Stat}

trait JavaCtorPrimaryRequiredResolver {
  def isRequired(ctorPrimary: Ctor.Primary, context: CtorRequiredResolutionContext): Boolean
}

private[resolvers] class JavaCtorPrimaryRequiredResolverImpl(ctorPrimaryClassifier: CtorPrimaryClassifier) extends JavaCtorPrimaryRequiredResolver {

  override def isRequired(ctorPrimary: Ctor.Primary, context: CtorRequiredResolutionContext): Boolean = {
    isNonDefault(ctorPrimary) ||
      haveParams(context.inits) ||
      context.terms.nonEmpty ||
      includeSecondaryCtors(context.otherStats)
  }

  private def haveParams(inits: List[Init]) = inits.headOption.exists(_.argss.nonEmpty)

  private def isNonDefault(ctorPrimary: Ctor.Primary) = !ctorPrimaryClassifier.isDefault(ctorPrimary)

  private def includeSecondaryCtors(stats: List[Stat]) = stats.collectFirst { case ctor: Ctor.Secondary => ctor }.isDefined
}

object JavaCtorPrimaryRequiredResolver extends JavaCtorPrimaryRequiredResolverImpl(CtorPrimaryClassifier)
