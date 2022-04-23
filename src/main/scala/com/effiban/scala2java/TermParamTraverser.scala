package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitModifiers}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Mod.Final
import scala.meta.Term

object TermParamTraverser extends ScalaTreeTraverser[Term.Param] {

  // method parameter declaration
  def traverse(termParam: Term.Param): Unit = {
    AnnotListTraverser.traverseMods(termParam.mods, onSameLine = true)
    val mods = javaOwnerContext match {
      case Lambda => termParam.mods
      case _ => termParam.mods :+ Final()
    }
    val modifierNames = JavaModifiersResolver.resolve(mods, List(classOf[Final]))
    emitModifiers(modifierNames)
    termParam.decltpe.foreach(declType => {
      GenericTreeTraverser.traverse(declType)
      emit(" ")
    })
    GenericTreeTraverser.traverse(termParam.name)
  }
}
