package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitTypeDeclaration

import scala.meta.Decl

object DeclTypeTraverser extends ScalaTreeTraverser[Decl.Type] {

  // Scala type declaration : Closest thing in Java is an empty interface with same params
  def traverse(typeDecl: Decl.Type): Unit = {
    emitTypeDeclaration(modifiers = JavaModifiersResolver.resolveForInterface(typeDecl.mods),
      typeKeyword = "interface",
      name = typeDecl.name.toString)
    TypeListTraverser.traverse(typeDecl.tparams)
    // TODO handle bounds properly
  }
}
