package effiban.scala2java.classifiers

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.JavaTreeType

import scala.meta.{Defn, Term}

trait DefnValClassifier {
  def isEnumConstantList(defnVal: Defn.Val, javaScope: JavaTreeType): Boolean
}

object DefnValClassifier extends DefnValClassifier {

  override def isEnumConstantList(defnVal: Defn.Val, javaScope: JavaTreeType): Boolean = {
    (defnVal.rhs, javaScope) match {
      case (Term.Name("Value"), JavaTreeType.Enum) => true
      case _ => false
    }
  }
}
