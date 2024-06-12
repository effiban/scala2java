package io.github.effiban.scala2java.core.declarationfinders

import scala.meta.{Defn, Pat, Term}

trait DefnVarTermNameDeclarationFinder {
  def find(defnVar: Defn.Var, termName: Term.Name): Option[Pat]
}

object DefnVarTermNameDeclarationFinder extends DefnVarTermNameDeclarationFinder {

  override def find(defnVar: Defn.Var, termName: Term.Name): Option[Pat] =
    defnVar.pats.flatMap(pat => find(pat, termName)).headOption

  private def find(pat: Pat, termName: Term.Name): List[Pat] = pat match {
    case patVar: Pat.Var if patVar.name.value == termName.value => List(patVar)
    case patTuple: Pat.Tuple => patTuple.args.flatMap(pat => find(pat, termName))
    case _ => Nil
  }
}
