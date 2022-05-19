package com.effiban.scala2java

import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Decl
import scala.meta.Mod.VarParam

trait DeclVarTraverser extends ScalaTreeTraverser[Decl.Var]

private[scala2java] class DeclVarTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaEmitter: JavaEmitter) extends DeclVarTraverser {
  import javaEmitter._

  override def traverse(varDecl: Decl.Var): Unit = {
    val annotationsOnSameLine = varDecl.mods.exists(_.isInstanceOf[VarParam])
    annotListTraverser.traverseMods(varDecl.mods, annotationsOnSameLine)
    val modifierNames = javaOwnerContext match {
      case Class => javaModifiersResolver.resolveForClassDataMember(varDecl.mods)
      case _ => Nil
    }
    emitModifiers(modifierNames)
    typeTraverser.traverse(varDecl.decltpe)
    emit(" ")
    // TODO - verify when not simple case
    patListTraverser.traverse(varDecl.pats)
  }
}

object DeclVarTraverser extends DeclVarTraverserImpl(
  AnnotListTraverser,
  TypeTraverser,
  PatListTraverser,
  JavaModifiersResolver
)
