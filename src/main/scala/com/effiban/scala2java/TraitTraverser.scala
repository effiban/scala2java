package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitTypeDeclaration
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Defn.Trait

trait TraitTraverser extends ScalaTreeTraverser[Trait]

private[scala2java] class TraitTraverserImpl(typeParamListTraverser: => TypeParamListTraverser,
                                             templateTraverser: => TemplateTraverser,
                                             javaModifiersResolver: JavaModifiersResolver) extends TraitTraverser {

  override def traverse(traitDef: Trait): Unit = {
    emitTypeDeclaration(modifiers = javaModifiersResolver.resolveForInterface(traitDef.mods),
      typeKeyword = "interface",
      name = traitDef.name.toString)
    typeParamListTraverser.traverse(traitDef.tparams)
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Interface
    templateTraverser.traverse(traitDef.templ)
    javaOwnerContext = outerJavaOwnerContext
  }
}

object TraitTraverser extends TraitTraverserImpl(TypeParamListTraverser, TemplateTraverser, JavaModifiersResolver)
