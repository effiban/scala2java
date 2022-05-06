package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitModifiers}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Decl
import scala.meta.Mod.{Final, ValParam}

trait DeclValTraverser extends ScalaTreeTraverser[Decl.Val]

private[scala2java] class DeclValTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaEmitter: JavaEmitter) extends DeclValTraverser {

  override def traverse(valDecl: Decl.Val): Unit = {
    val annotationsOnSameLine = valDecl.mods.exists(_.isInstanceOf[ValParam])
    annotListTraverser.traverseMods(valDecl.mods, annotationsOnSameLine)
    val mods = valDecl.mods :+ Final()
    val modifierNames = javaOwnerContext match {
      case Class => javaModifiersResolver.resolveForClassDataMember(mods)
      case _ if javaOwnerContext == Interface => Nil
      // The only possible modifier for a local var is 'final'
      case Method => javaModifiersResolver.resolve(mods, List(classOf[Final]))
      case _ => Nil
    }
    emitModifiers(modifierNames)
    typeTraverser.traverse(valDecl.decltpe)
    emit(" ")
    // TODO - verify when not simple case
    patListTraverser.traverse(valDecl.pats)
  }
}

object DeclValTraverser extends DeclValTraverserImpl(
  AnnotListTraverser,
  TypeTraverser,
  PatListTraverser,
  JavaModifiersResolver
)
