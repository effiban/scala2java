package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitTypeDeclaration
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Defn.Trait

object TraitTraverser extends ScalaTreeTraverser[Trait] {

  def traverse(traitDef: Trait): Unit = {
    emitTypeDeclaration(modifiers = JavaModifiersResolver.resolveForInterface(traitDef.mods),
      typeKeyword = "interface",
      name = traitDef.name.toString)
    // TODO - traverse type params
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Interface
    GenericTreeTraverser.traverse(traitDef.templ)
    javaOwnerContext = outerJavaOwnerContext
  }
}
