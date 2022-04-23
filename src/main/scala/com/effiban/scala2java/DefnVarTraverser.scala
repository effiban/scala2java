package com.effiban.scala2java

import com.effiban.scala2java.GenericTreeTraverser.{resolveJavaClassDataMemberExplicitModifiers, traverseAnnotations}
import com.effiban.scala2java.JavaEmitter.{emit, emitModifiers}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Defn
import scala.meta.Mod.{Annot, ValParam}

object DefnVarTraverser extends ScalaTreeTraverser[Defn.Var] {

  def traverse(varDef: Defn.Var): Unit = {
    val annotationsOnSameLine = varDef.mods.exists(_.isInstanceOf[ValParam])
    traverseAnnotations(varDef.mods.collect { case ann: Annot => ann }, annotationsOnSameLine)
    val modifierNames = varDef.mods match {
      case modifiers if javaOwnerContext == Class => resolveJavaClassDataMemberExplicitModifiers(modifiers)
      case _ => Nil
    }
    emitModifiers(modifierNames)
    varDef.decltpe match {
      case Some(declType) =>
        GenericTreeTraverser.traverse(declType)
        emit(" ")
      case None if javaOwnerContext == Method => emit("var ")
      case _ =>
    }
    varDef.pats.foreach(GenericTreeTraverser.traverse)
    varDef.rhs.foreach { rhs =>
      emit(" = ")
      GenericTreeTraverser.traverse(rhs)
    }
  }
}
