package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Decl

trait DeclTraverser {
  def traverse(decl: Decl, context: StatContext = StatContext()): Unit
}

private[traversers] class DeclTraverserImpl(declValTraverser: => DeclValTraverser,
                                            declVarTraverser: => DeclVarTraverser,
                                            declDefTraverser: => DeclDefTraverser,
                                            declTypeTraverser: => DeclTypeTraverser)
                                           (implicit javaWriter: JavaWriter) extends DeclTraverser {

  import javaWriter._

  override def traverse(decl: Decl, context: StatContext = StatContext()): Unit = decl match {
    case valDecl: Decl.Val => declValTraverser.traverse(valDecl, context)
    case varDecl: Decl.Var => declVarTraverser.traverse(varDecl, context)
    case defDecl: Decl.Def => declDefTraverser.traverse(defDecl, context)
    case typeDecl: Decl.Type => declTypeTraverser.traverse(typeDecl, context)
    case _ => writeComment(s"UNSUPPORTED: $decl")
  }
}
