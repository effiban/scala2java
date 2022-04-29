package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Decl

trait DeclTraverser extends ScalaTreeTraverser[Decl]

object DeclTraverser extends DeclTraverser {

  override def traverse(decl: Decl): Unit = decl match {
    case valDecl: Decl.Val => DeclValTraverser.traverse(valDecl)
    case varDecl: Decl.Var => DeclVarTraverser.traverse(varDecl)
    case defDecl: Decl.Def => DeclDefTraverser.traverse(defDecl)
    case typeDecl: Decl.Type => DeclTypeTraverser.traverse(typeDecl)
    case _ => emitComment(s"UNSUPPORTED: $decl")
  }
}
