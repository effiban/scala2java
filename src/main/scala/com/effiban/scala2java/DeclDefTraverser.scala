package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter._
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Decl

trait DeclDefTraverser extends ScalaTreeTraverser[Decl.Def]

object DeclDefTraverser extends DeclDefTraverser {

  override def traverse(defDecl: Decl.Def): Unit = {
    emitLine()
    AnnotListTraverser.traverseMods(defDecl.mods)
    val resolvedModifierNames = javaOwnerContext match {
      case Interface => JavaModifiersResolver.resolveForInterfaceMethod(defDecl.mods, hasBody = false)
      case Class => JavaModifiersResolver.resolveForClassMethod(defDecl.mods)
      case _ => Nil
    }
    emitModifiers(resolvedModifierNames)
    TypeTraverser.traverse(defDecl.decltpe)
    emit(" ")
    TermNameTraverser.traverse(defDecl.name)
    // TODO handle method type params

    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Method
    TermParamListTraverser.traverse(defDecl.paramss.flatten)
    javaOwnerContext = outerJavaOwnerContext
  }
}
