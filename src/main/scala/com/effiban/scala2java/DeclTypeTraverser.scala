package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitTypeDeclaration

import scala.meta.Decl

trait DeclTypeTraverser extends ScalaTreeTraverser[Decl.Type]

object DeclTypeTraverser extends DeclTypeTraverser {

  // Scala type declaration : Closest thing in Java is an empty interface with same params
  override def traverse(typeDecl: Decl.Type): Unit = {
    emitTypeDeclaration(modifiers = JavaModifiersResolver.resolveForInterface(typeDecl.mods),
      typeKeyword = "interface",
      name = typeDecl.name.toString)
    TypeParamListTraverser.traverse(typeDecl.tparams)
    // TODO handle bounds properly
  }
}
