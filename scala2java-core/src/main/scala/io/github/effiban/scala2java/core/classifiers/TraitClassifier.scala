package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.{Defn, Type}

trait TraitClassifier {
  def isEnumTypeDef(defnTrait: Defn.Trait, javaScope: JavaScope): Boolean
}

object TraitClassifier extends TraitClassifier {

  override def isEnumTypeDef(defnTrait: Defn.Trait, javaScope: JavaScope): Boolean = {
    (defnTrait.templ.inits.map(_.tpe), javaScope) match {
      case (List(Type.Name("Value")), JavaScope.Enum) => true
      case _ => false
    }
  }
}
