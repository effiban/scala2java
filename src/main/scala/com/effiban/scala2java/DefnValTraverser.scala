package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitModifiers}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Defn
import scala.meta.Mod.{Final, ValParam}

trait DefnValTraverser extends ScalaTreeTraverser[Defn.Val]

private[scala2java] class DefnValTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               termTraverser: => TermTraverser,
                                               javaModifiersResolver: JavaModifiersResolver) extends DefnValTraverser {

  def traverse(valDef: Defn.Val): Unit = {
    val annotationsOnSameLine = valDef.mods.exists(_.isInstanceOf[ValParam])
    annotListTraverser.traverseMods(valDef.mods, annotationsOnSameLine)
    val mods = valDef.mods :+ Final()
    val modifierNames = mods match {
      case modifiers if javaOwnerContext == Class => javaModifiersResolver.resolveForClassDataMember(modifiers)
      case _ if javaOwnerContext == Interface => Nil
      // The only possible modifier for a method param or local var is 'final'
      case modifiers if javaOwnerContext == Method => javaModifiersResolver.resolve(modifiers, List(classOf[Final]))
      case _ => Nil
    }
    emitModifiers(modifierNames)
    valDef.decltpe match {
      case Some(declType) =>
        typeTraverser.traverse(declType)
        emit(" ")
      case None if javaOwnerContext == Method => emit("var ")
      case _ =>
    }
    // TODO verify for non-simple case
    patListTraverser.traverse(valDef.pats)
    emit(" = ")
    termTraverser.traverse(valDef.rhs)
  }
}

object DefnValTraverser extends DefnValTraverserImpl(
  AnnotListTraverser,
  TypeTraverser,
  PatListTraverser,
  TermTraverser,
  JavaModifiersResolver
)

