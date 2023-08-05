package io.github.effiban.scala2java.core.classifiers

import scala.meta.{Init, Type}

trait InitClassifier {
  def isEnum(init: Init): Boolean
}

object InitClassifier extends InitClassifier {

  override def isEnum(init: Init): Boolean = init.tpe match {
    case Type.Name("Enumeration") => true
    case _ => false
  }
}
