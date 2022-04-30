package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitTypeDeclaration}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Defn

trait DefnTypeTraverser extends ScalaTreeTraverser[Defn.Type]

private[scala2java] class DefnTypeTraverserImpl(typeParamListTraverser: => TypeParamListTraverser,
                                                typeTraverser: => TypeTraverser,
                                                javaModifiersResolver: JavaModifiersResolver) extends DefnTypeTraverser {

  // Scala type definition : Closest thing in Java is an empty interface extending the same RHS
  override def traverse(typeDef: Defn.Type): Unit = {
    emitTypeDeclaration(modifiers = javaModifiersResolver.resolveForInterface(typeDef.mods),
      typeKeyword = "interface",
      name = typeDef.name.toString)
    typeParamListTraverser.traverse(typeDef.tparams)
    emit(" extends ") // TODO handle bounds properly
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Interface
    typeTraverser.traverse(typeDef.body)
    javaOwnerContext = outerJavaOwnerContext
  }
}

object DefnTypeTraverser extends DefnTypeTraverserImpl(
  TypeParamListTraverser,
  TypeTraverser,
  JavaModifiersResolver)
