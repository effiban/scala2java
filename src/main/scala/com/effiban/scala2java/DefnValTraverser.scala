package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitModifiers}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Defn
import scala.meta.Mod.{Final, ValParam}

object DefnValTraverser extends ScalaTreeTraverser[Defn.Val] {

  def traverse(valDef: Defn.Val): Unit = {
    val annotationsOnSameLine = valDef.mods.exists(_.isInstanceOf[ValParam])
    AnnotListTraverser.traverseMods(valDef.mods, annotationsOnSameLine)
    val mods = valDef.mods :+ Final()
    val modifierNames = mods match {
      case modifiers if javaOwnerContext == Class => JavaModifiersResolver.resolveForClassDataMember(modifiers)
      case _ if javaOwnerContext == Interface => Nil
      // The only possible modifier for a method param or local var is 'final' (if it's immutable as determined above)
      case modifiers if javaOwnerContext == Method => JavaModifiersResolver.resolve(modifiers, List(classOf[Final]))
      case _ => Nil
    }
    emitModifiers(modifierNames)
    valDef.decltpe match {
      case Some(declType) =>
        TypeTraverser.traverse(declType)
        emit(" ")
      case None if javaOwnerContext == Method => emit("var ")
      case _ =>
    }
    // TODO verify for non-simple case
    PatListTraverser.traverse(valDef.pats)
    emit(" = ")
    TermTraverser.traverse(valDef.rhs)
  }
}
