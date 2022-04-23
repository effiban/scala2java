package com.effiban.scala2java

import com.effiban.scala2java.GenericTreeTraverser.{resolveJavaInterfaceExplicitModifiers, traverseGenericTypeList}
import com.effiban.scala2java.JavaEmitter.{emit, emitTypeDeclaration}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Defn

object DefnTypeTraverser extends ScalaTreeTraverser[Defn.Type] {

  // Scala type definition : Closest thing in Java is an empty interface extending the same RHS
  def traverse(typeDef: Defn.Type): Unit = {
    emitTypeDeclaration(modifiers = resolveJavaInterfaceExplicitModifiers(typeDef.mods),
      typeKeyword = "interface",
      name = typeDef.name.toString)
    traverseGenericTypeList(typeDef.tparams)
    emit(" extends ") // TODO handle bounds properly
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Interface
    GenericTreeTraverser.traverse(typeDef.body)
    javaOwnerContext = outerJavaOwnerContext
  }
}
