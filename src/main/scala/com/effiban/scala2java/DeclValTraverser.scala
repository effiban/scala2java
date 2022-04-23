package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitModifiers}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Decl
import scala.meta.Mod.{Final, ValParam}

object DeclValTraverser extends ScalaTreeTraverser[Decl.Val] {

  override def traverse(valDecl: Decl.Val): Unit = {
    val annotationsOnSameLine = valDecl.mods.exists(_.isInstanceOf[ValParam])
    AnnotListTraverser.traverseMods(valDecl.mods, annotationsOnSameLine)
    val mods = valDecl.mods :+ Final()
    val modifierNames = javaOwnerContext match {
      case Class => JavaModifiersResolver.resolveForClassDataMember(mods)
      case _ if javaOwnerContext == Interface => Nil
      // The only possible modifier for a local var is 'final'
      case Method => JavaModifiersResolver.resolve(mods, List(classOf[Final]))
      case _ => Nil
    }
    emitModifiers(modifierNames)
    TypeTraverser.traverse(valDecl.decltpe)
    emit(" ")
    // TODO - verify when not simple case
    PatListTraverser.traverse(valDecl.pats)
  }
}
