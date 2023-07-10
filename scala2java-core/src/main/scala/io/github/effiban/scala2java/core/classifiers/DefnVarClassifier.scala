package io.github.effiban.scala2java.core.classifiers

import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.{Defn, Mod, Term}

trait DefnVarClassifier {
  def isEnumConstantList(defnVar: Defn.Var, javaScope: JavaScope): Boolean
}

object DefnVarClassifier extends DefnVarClassifier {

  override def isEnumConstantList(defnVar: Defn.Var, javaScope: JavaScope): Boolean = {
    (defnVar.mods, defnVar.rhs, javaScope) match {
      case (mods, Some(Term.Name("Value")), JavaScope.Enum) if mods.exists(_.isInstanceOf[Mod.Final]) => true
      case _ => false
    }
  }
}
