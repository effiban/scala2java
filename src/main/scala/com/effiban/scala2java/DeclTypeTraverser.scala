package com.effiban.scala2java

import com.effiban.scala2java.GenericTreeTraverser.{resolveJavaInterfaceExplicitModifiers, traverseGenericTypeList}
import com.effiban.scala2java.JavaEmitter.emitTypeDeclaration

import scala.meta.Decl

object DeclTypeTraverser extends ScalaTreeTraverser[Decl.Type] {

  // Scala type declaration : Closest thing in Java is an empty interface with same params
  def traverse(typeDecl: Decl.Type): Unit = {
    emitTypeDeclaration(modifiers = resolveJavaInterfaceExplicitModifiers(typeDecl.mods),
      typeKeyword = "interface",
      name = typeDecl.name.toString)
    traverseGenericTypeList(typeDecl.tparams)
    // TODO handle bounds properly
  }
}
