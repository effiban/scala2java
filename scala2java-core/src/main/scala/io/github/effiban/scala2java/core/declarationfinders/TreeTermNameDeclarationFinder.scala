package io.github.effiban.scala2java.core.declarationfinders

import scala.meta.{Decl, Defn, Term, Tree}

trait TreeTermNameDeclarationFinder {
  def find(tree: Tree, termName: Term.Name): Option[Tree]
}

private[declarationfinders] class TreeTermNameDeclarationFinderImpl(
  termParamTermNameDeclarationFinder: TermParamTermNameDeclarationFinder,
  declVarTermNameDeclarationFinder: DeclVarTermNameDeclarationFinder,
  defnVarTermNameDeclarationFinder: DefnVarTermNameDeclarationFinder) extends TreeTermNameDeclarationFinder {

  override def find(tree: Tree, termName: Term.Name): Option[Tree] = tree match {
    case termParam: Term.Param => termParamTermNameDeclarationFinder.find(termParam, termName)
    case declVar: Decl.Var => declVarTermNameDeclarationFinder.find(declVar, termName)
    case defnVar: Defn.Var => defnVarTermNameDeclarationFinder.find(defnVar, termName)
    case _ => None
  }
}

object TreeTermNameDeclarationFinder extends TreeTermNameDeclarationFinderImpl(
  TermParamTermNameDeclarationFinder,
  DeclVarTermNameDeclarationFinder,
  DefnVarTermNameDeclarationFinder
)
