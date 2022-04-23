package com.effiban.scala2java

import com.effiban.scala2java.GenericTreeTraverser.{resolveJavaClassDataMemberExplicitModifiers, traverseAnnotations}
import com.effiban.scala2java.JavaEmitter.{emit, emitModifiers}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Decl
import scala.meta.Mod.{Annot, ValParam}

object DeclVarTraverser extends ScalaTreeTraverser[Decl.Var] {

  override def traverse(varDecl: Decl.Var): Unit = {
    val annotationsOnSameLine = varDecl.mods.exists(_.isInstanceOf[ValParam])
    traverseAnnotations(varDecl.mods.collect { case ann: Annot => ann }, annotationsOnSameLine)
    val modifierNames = javaOwnerContext match {
      case Class => resolveJavaClassDataMemberExplicitModifiers(varDecl.mods)
      case _ => Nil
    }
    emitModifiers(modifierNames)
    GenericTreeTraverser.traverse(varDecl.decltpe)
    emit(" ")
    varDecl.pats.foreach(GenericTreeTraverser.traverse)
  }
}
