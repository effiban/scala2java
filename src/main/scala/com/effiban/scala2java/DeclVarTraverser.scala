package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitModifiers}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Decl
import scala.meta.Mod.ValParam

trait DeclVarTraverser extends ScalaTreeTraverser[Decl.Var]

object DeclVarTraverser extends DeclVarTraverser {

  override def traverse(varDecl: Decl.Var): Unit = {
    val annotationsOnSameLine = varDecl.mods.exists(_.isInstanceOf[ValParam])
    AnnotListTraverser.traverseMods(varDecl.mods, annotationsOnSameLine)
    val modifierNames = javaOwnerContext match {
      case Class => JavaModifiersResolver.resolveForClassDataMember(varDecl.mods)
      case _ => Nil
    }
    emitModifiers(modifierNames)
    TypeTraverser.traverse(varDecl.decltpe)
    emit(" ")
    // TODO - verify when not simple case
    PatListTraverser.traverse(varDecl.pats)
  }
}
