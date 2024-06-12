package io.github.effiban.scala2java.core.declarationfinders

import scala.meta.{Decl, Term, Tree}

trait TreeTermNameDeclarationFinder {
  def find(tree: Tree, termName: Term.Name): Option[Tree]
}

private[declarationfinders] class TreeTermNameDeclarationFinderImpl(
  termParamTermNameDeclarationFinder: TermParamTermNameDeclarationFinder,
  declVarTermNameDeclarationFinder: DeclVarTermNameDeclarationFinder) extends TreeTermNameDeclarationFinder {

  override def find(tree: Tree, termName: Term.Name): Option[Tree] = tree match {
    case termParam: Term.Param => termParamTermNameDeclarationFinder.find(termParam, termName)
    case declVar: Decl.Var => declVarTermNameDeclarationFinder.find(declVar, termName)
    case _ => None
  }
}

object TreeTermNameDeclarationFinder extends TreeTermNameDeclarationFinderImpl(
  TermParamTermNameDeclarationFinder,
  DeclVarTermNameDeclarationFinder
)
