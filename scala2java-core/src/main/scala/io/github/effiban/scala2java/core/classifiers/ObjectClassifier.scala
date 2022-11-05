package io.github.effiban.scala2java.core.classifiers

import scala.meta.Defn

trait ObjectClassifier {
  def isStandalone(defnObject: Defn.Object): Boolean
}

object ObjectClassifier extends ObjectClassifier {

  override def isStandalone(defnObject: Defn.Object): Boolean = defnObject.templ.inits.isEmpty
}
