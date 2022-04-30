package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitModifiers}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Defn
import scala.meta.Mod.ValParam

trait DefnVarTraverser extends ScalaTreeTraverser[Defn.Var]

private[scala2java] class DefnVarTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               termTraverser: => TermTraverser,
                                               javaModifiersResolver: JavaModifiersResolver) extends DefnVarTraverser {

  override def traverse(varDef: Defn.Var): Unit = {
    val annotationsOnSameLine = varDef.mods.exists(_.isInstanceOf[ValParam])
    annotListTraverser.traverseMods(varDef.mods, annotationsOnSameLine)
    val modifierNames = varDef.mods match {
      case modifiers if javaOwnerContext == Class => javaModifiersResolver.resolveForClassDataMember(modifiers)
      case _ => Nil
    }
    emitModifiers(modifierNames)
    varDef.decltpe match {
      case Some(declType) =>
        typeTraverser.traverse(declType)
        emit(" ")
      case None if javaOwnerContext == Method => emit("var ")
      case _ =>
    }
    // TODO - verify this
    patListTraverser.traverse(varDef.pats)
    varDef.rhs.foreach { rhs =>
      emit(" = ")
      termTraverser.traverse(rhs)
    }
  }
}

object DefnVarTraverser extends DefnVarTraverserImpl(AnnotListTraverser,
  TypeTraverser,
  PatListTraverser,
  TermTraverser,
  JavaModifiersResolver
)
