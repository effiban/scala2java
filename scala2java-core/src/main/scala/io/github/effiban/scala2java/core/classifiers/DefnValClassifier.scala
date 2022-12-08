package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.{Defn, Term}

trait DefnValClassifier {
  def isEnumConstantList(defnVal: Defn.Val, javaScope: JavaScope): Boolean
}

object DefnValClassifier extends DefnValClassifier {

  override def isEnumConstantList(defnVal: Defn.Val, javaScope: JavaScope): Boolean = {
    (defnVal.rhs, javaScope) match {
      case (Term.Name("Value"), JavaScope.Enum) => true
      case _ => false
    }
  }
}
