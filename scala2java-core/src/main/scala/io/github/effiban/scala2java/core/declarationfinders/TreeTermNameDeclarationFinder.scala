package io.github.effiban.scala2java.core.declarationfinders

import scala.meta.{Decl, Defn, Template, Term, Tree}

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
    case declDef: Decl.Def => find(declDef.paramss.flatten, termName)
    case defnDef: Defn.Def => find(defnDef.paramss.flatten, termName)
    case function: Term.Function => find(function.params, termName)
    case aClass: Defn.Class => find(aClass.ctor.paramss.flatten, termName)
    case _ => None
  }

  private def find(params: List[Term.Param], termName: Term.Name) = {
    params.flatMap(param => termParamTermNameDeclarationFinder.find(param, termName)).headOption
  }
}

object TreeTermNameDeclarationFinder extends TreeTermNameDeclarationFinderImpl(
  TermParamTermNameDeclarationFinder,
  DeclVarTermNameDeclarationFinder,
  DefnVarTermNameDeclarationFinder
)
