package effiban.scala2java.classifiers

import effiban.scala2java.entities.JavaScope
import effiban.scala2java.entities.JavaScope.JavaScope

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
