package io.github.effiban.scala2java.core.declarationfinders

import scala.meta.{Term, Tree}

trait TreeTermNameDeclarationFinder {
  def find(tree: Tree, termName: Term.Name): Option[Tree]
}

private[declarationfinders] class TreeTermNameDeclarationFinderImpl(
  termParamTermNameDeclarationFinder: TermParamTermNameDeclarationFinder) extends TreeTermNameDeclarationFinder {

  override def find(tree: Tree, termName: Term.Name): Option[Tree] = tree match {
    case termParam: Term.Param => termParamTermNameDeclarationFinder.find(termParam, termName)
    case _ => None
  }
}

object TreeTermNameDeclarationFinder extends TreeTermNameDeclarationFinderImpl(
  TermParamTermNameDeclarationFinder
)
