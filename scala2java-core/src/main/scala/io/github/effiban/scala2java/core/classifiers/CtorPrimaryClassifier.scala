package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.core.contexts.CtorRequiredResolutionContext

import scala.meta.{Ctor, Init, Stat}

trait CtorPrimaryClassifier {

  def isDefault(ctorPrimary: Ctor.Primary): Boolean
}

object CtorPrimaryClassifier extends CtorPrimaryClassifier {

  override def isDefault(ctorPrimary: Ctor.Primary): Boolean = {
    ctorPrimary match {
      case Ctor.Primary(Nil, _, argss) if argss.flatten.isEmpty => true
      case _ => false
    }
  }
}
