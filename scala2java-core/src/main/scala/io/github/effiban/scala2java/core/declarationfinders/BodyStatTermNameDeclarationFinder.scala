package io.github.effiban.scala2java.core.declarationfinders

import scala.meta.{Decl, Defn, Member, Stat, Term, Tree}

trait BodyStatTermNameDeclarationFinder {
  def find(stat: Stat, termName: Term.Name): Option[Tree]
}

private[declarationfinders] class BodyStatTermNameDeclarationFinderImpl(
  declVarTermNameDeclarationFinder: DeclVarTermNameDeclarationFinder,
  defnVarTermNameDeclarationFinder: DefnVarTermNameDeclarationFinder) extends BodyStatTermNameDeclarationFinder {

  override def find(stat: Stat, termName: Term.Name): Option[Tree] = stat match {
    case declVar: Decl.Var => declVarTermNameDeclarationFinder.find(declVar, termName)
    case defnVar: Defn.Var => defnVarTermNameDeclarationFinder.find(defnVar, termName)
    case memberTerm: Member.Term if memberTerm.name.structure == termName.structure => Some(memberTerm)
    case _ => None
  }
}

object BodyStatTermNameDeclarationFinder extends BodyStatTermNameDeclarationFinderImpl(
  DeclVarTermNameDeclarationFinder,
  DefnVarTermNameDeclarationFinder
)
