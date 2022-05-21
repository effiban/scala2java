package com.effiban.scala2java

import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Decl
import scala.meta.Mod.{Final, ValParam}

trait DeclValTraverser extends ScalaTreeTraverser[Decl.Val]

//TODO - if Java owner is an interface, the output should be an accessor method
private[scala2java] class DeclValTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               patListTraverser: => PatListTraverser,
                                               javaModifiersResolver: JavaModifiersResolver)
                                              (implicit javaEmitter: JavaEmitter) extends DeclValTraverser {
  import javaEmitter._

  override def traverse(valDecl: Decl.Val): Unit = {
    val annotationsOnSameLine = valDecl.mods.exists(_.isInstanceOf[ValParam])
    annotListTraverser.traverseMods(valDecl.mods, annotationsOnSameLine)
    val mods = valDecl.mods :+ Final()
    val modifierNames = javaOwnerContext match {
      case Class => javaModifiersResolver.resolveForClassDataMember(mods)
      //TODO replace interface data member (invalid in Java) with method
      case _ if javaOwnerContext == Interface => Nil
      // The only possible modifier for a local var is 'final'
      case Method => javaModifiersResolver.resolve(mods, List(classOf[Final]))
      case _ => Nil
    }
    emitModifiers(modifierNames)
    typeTraverser.traverse(valDecl.decltpe)
    emit(" ")
    //TODO - verify when not simple case
    patListTraverser.traverse(valDecl.pats)
  }
}

object DeclValTraverser extends DeclValTraverserImpl(
  AnnotListTraverser,
  TypeTraverser,
  PatListTraverser,
  JavaModifiersResolver
)
