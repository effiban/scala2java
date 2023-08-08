package io.github.effiban.scala2java.core.traversers

import scala.meta.Decl

trait DeclTraverser {
  def traverse(decl: Decl): Decl

}

private[traversers] class DeclTraverserImpl(declVarTraverser: => DeclVarTraverser,
                                            declDefTraverser: => DeclDefTraverser) extends DeclTraverser {

  override def traverse(decl: Decl): Decl = decl match {
    case varDecl: Decl.Var => declVarTraverser.traverse(varDecl)
    case defDecl: Decl.Def => declDefTraverser.traverse(defDecl)
    case decl => decl
  }
}
