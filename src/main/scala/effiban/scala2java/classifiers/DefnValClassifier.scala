package effiban.scala2java.classifiers

import effiban.scala2java.entities.JavaScope
import effiban.scala2java.entities.JavaScope.JavaScope

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
