package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.{Defn, Type}

trait DefnTypeClassifier {
  def isEnumTypeDef(defnType: Defn.Type, javaScope: JavaScope): Boolean
}

object DefnTypeClassifier extends DefnTypeClassifier {

  override def isEnumTypeDef(defnType: Defn.Type, javaScope: JavaScope): Boolean = {
    (defnType.body, javaScope) match {
      case (Type.Name("Value"), JavaScope.Enum) => true
      case _ => false
    }
  }
}
