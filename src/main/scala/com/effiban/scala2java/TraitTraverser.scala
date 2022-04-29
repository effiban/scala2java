package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitTypeDeclaration
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Defn.Trait

trait TraitTraverser extends ScalaTreeTraverser[Trait]

object TraitTraverser extends TraitTraverser {

  override def traverse(traitDef: Trait): Unit = {
    emitTypeDeclaration(modifiers = JavaModifiersResolver.resolveForInterface(traitDef.mods),
      typeKeyword = "interface",
      name = traitDef.name.toString)
    TypeParamListTraverser.traverse(traitDef.tparams)
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Interface
    TemplateTraverser.traverse(traitDef.templ)
    javaOwnerContext = outerJavaOwnerContext
  }
}
