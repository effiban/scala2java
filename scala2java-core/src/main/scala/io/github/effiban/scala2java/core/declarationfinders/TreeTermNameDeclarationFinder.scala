package io.github.effiban.scala2java.core.declarationfinders

import scala.meta.Term.Block
import scala.meta.{Decl, Defn, Pkg, Stat, Template, Term, Tree}

trait TreeTermNameDeclarationFinder {
  def find(tree: Tree, termName: Term.Name): Option[Tree]
}

private[declarationfinders] class TreeTermNameDeclarationFinderImpl(
  termParamTermNameDeclarationFinder: TermParamTermNameDeclarationFinder,
  declVarTermNameDeclarationFinder: DeclVarTermNameDeclarationFinder,
  defnVarTermNameDeclarationFinder: DefnVarTermNameDeclarationFinder,
  bodyStatTermNameDeclarationFinder: BodyStatTermNameDeclarationFinder) extends TreeTermNameDeclarationFinder {

  override def find(tree: Tree, termName: Term.Name): Option[Tree] = tree match {
    case termParam: Term.Param => termParamTermNameDeclarationFinder.find(termParam, termName)
    case declVar: Decl.Var => declVarTermNameDeclarationFinder.find(declVar, termName)
    case defnVar: Defn.Var => defnVarTermNameDeclarationFinder.find(defnVar, termName)
    case declDef: Decl.Def => findInParams(declDef.paramss.flatten, termName)
    case defnDef: Defn.Def => findInParams(defnDef.paramss.flatten, termName)
    case function: Term.Function => findInParams(function.params, termName)
    case aClass: Defn.Class => findInParams(aClass.ctor.paramss.flatten, termName)
    case block: Block => findInStats(block.stats, termName)
    case template: Template => findInStats(template.stats, termName)
    case pkg: Pkg => findInStats(pkg.stats, termName)
    case _ => None
  }

  private def findInParams(params: List[Term.Param], termName: Term.Name) = {
    params.flatMap(param => termParamTermNameDeclarationFinder.find(param, termName)).headOption
  }

  private def findInStats(stats: List[Stat], termName: Term.Name) = {
    stats.flatMap(stat => bodyStatTermNameDeclarationFinder.find(stat, termName)).headOption
  }
}

object TreeTermNameDeclarationFinder extends TreeTermNameDeclarationFinderImpl(
  TermParamTermNameDeclarationFinder,
  DeclVarTermNameDeclarationFinder,
  DefnVarTermNameDeclarationFinder,
  BodyStatTermNameDeclarationFinder
)
