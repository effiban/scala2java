package com.effiban.scala2java

import com.effiban.scala2java.GenericTreeTraverser.{resolveJavaClassMethodExplicitModifiers, resolveJavaInterfaceMethodExplicitModifiers, traverseAnnotations}
import com.effiban.scala2java.JavaEmitter._
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Decl
import scala.meta.Mod.Annot

object DeclDefTraverser extends ScalaTreeTraverser[Decl.Def] {

  override def traverse(defDecl: Decl.Def): Unit = {
    emitLine()
    traverseAnnotations(defDecl.mods.collect { case ann: Annot => ann })
    val resolvedModifierNames = javaOwnerContext match {
      case Interface => resolveJavaInterfaceMethodExplicitModifiers(defDecl.mods, hasBody = false)
      case Class => resolveJavaClassMethodExplicitModifiers(defDecl.mods)
      case _ => Nil
    }
    emitModifiers(resolvedModifierNames)
    GenericTreeTraverser.traverse(defDecl.decltpe)
    emit(" ")
    GenericTreeTraverser.traverse(defDecl.name)
    // TODO handle method type params

    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Method
    traverseMethodParams(defDecl)
    javaOwnerContext = outerJavaOwnerContext
  }

  def traverseMethodParams(defDecl: Decl.Def): Unit = {
    emitParametersStart()
    val params = defDecl.paramss.flatten
    GenericTreeTraverser.traverse(params)
    emitParametersEnd()
  }
}
