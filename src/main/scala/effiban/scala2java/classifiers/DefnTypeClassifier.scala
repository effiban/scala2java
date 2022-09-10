package effiban.scala2java.classifiers

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.JavaTreeType

import scala.meta.{Defn, Type}

trait DefnTypeClassifier {
  def isEnumTypeDef(defnType: Defn.Type, javaScope: JavaTreeType): Boolean
}

object DefnTypeClassifier extends DefnTypeClassifier {

  override def isEnumTypeDef(defnType: Defn.Type, javaScope: JavaTreeType): Boolean = {
    (defnType.body, javaScope) match {
      case (Type.Name("Value"), JavaTreeType.Enum) => true
      case _ => false
    }
  }
}
