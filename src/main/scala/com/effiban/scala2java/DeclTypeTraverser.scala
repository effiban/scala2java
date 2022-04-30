package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitTypeDeclaration

import scala.meta.Decl

trait DeclTypeTraverser extends ScalaTreeTraverser[Decl.Type]

private[scala2java] class DeclTypeTraverserImpl(typeParamListTraverser: => TypeParamListTraverser,
                                                javaModifiersResolver: JavaModifiersResolver) extends DeclTypeTraverser {

  // Scala type declaration : Closest thing in Java is an empty interface with same params
  override def traverse(typeDecl: Decl.Type): Unit = {
    emitTypeDeclaration(modifiers = javaModifiersResolver.resolveForInterface(typeDecl.mods),
      typeKeyword = "interface",
      name = typeDecl.name.toString)
    typeParamListTraverser.traverse(typeDecl.tparams)
    // TODO handle bounds properly
  }
}

object DeclTypeTraverser extends DeclTypeTraverserImpl(TypeParamListTraverser, JavaModifiersResolver)
