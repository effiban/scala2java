package io.github.effiban.scala2java.core.classifiers

import scala.meta.{Defn, Mod}

trait ClassClassifier {
  def isCase(defnClass: Defn.Class): Boolean
  def isRegular(defnClass: Defn.Class): Boolean
}

object ClassClassifier extends ClassClassifier {

  override def isCase(defnClass: Defn.Class): Boolean = defnClass.mods.exists(_.isInstanceOf[Mod.Case])

  override def isRegular(defnClass: Defn.Class): Boolean = !isCase(defnClass)
}
