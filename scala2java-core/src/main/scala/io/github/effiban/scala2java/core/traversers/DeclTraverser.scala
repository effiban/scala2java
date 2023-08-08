package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.traversers.results.UnsupportedDeclTraversalResult

import scala.meta.Decl

trait DeclTraverser {
  def traverse(decl: Decl, context: StatContext = StatContext()): Decl

}

private[traversers] class DeclTraverserImpl(declVarTraverser: => DeclVarTraverser,
                                            declDefTraverser: => DeclDefTraverser) extends DeclTraverser {

  override def traverse(decl: Decl, context: StatContext = StatContext()): Decl = decl match {
    case varDecl: Decl.Var => declVarTraverser.traverse(varDecl, context)
    case defDecl: Decl.Def => declDefTraverser.traverse(defDecl, context).tree
    case decl => UnsupportedDeclTraversalResult(decl).tree
  }
}
