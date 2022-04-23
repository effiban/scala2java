package com.effiban.scala2java

import com.effiban.scala2java.GenericTreeTraverser.{resolveJavaClassDataMemberExplicitModifiers, resolveJavaExplicitModifiers}
import com.effiban.scala2java.JavaEmitter.{emit, emitModifiers}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Decl
import scala.meta.Mod.{Annot, Final, ValParam}

object DeclValTraverser extends ScalaTreeTraverser[Decl.Val] {

  override def traverse(valDecl: Decl.Val): Unit = {
    val annotationsOnSameLine = valDecl.mods.exists(_.isInstanceOf[ValParam])
    GenericTreeTraverser.traverseAnnotations(valDecl.mods.collect { case ann: Annot => ann }, annotationsOnSameLine)
    val mods = valDecl.mods :+ Final()
    val modifierNames = javaOwnerContext match {
      case Class => resolveJavaClassDataMemberExplicitModifiers(mods)
      case _ if javaOwnerContext == Interface => Nil
      // The only possible modifier for a local var is 'final'
      case Method => resolveJavaExplicitModifiers(mods, List(classOf[Final]))
      case _ => Nil
    }
    emitModifiers(modifierNames)
    GenericTreeTraverser.traverse(valDecl.decltpe)
    emit(" ")
    valDecl.pats.foreach(GenericTreeTraverser.traverse)
  }


}
