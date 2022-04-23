package com.effiban.scala2java

import com.effiban.scala2java.GenericTreeTraverser.resolveJavaClassExplicitModifiers
import com.effiban.scala2java.JavaEmitter.{emitComment, emitLine, emitTypeDeclaration}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Defn

object ObjectTraverser extends ScalaTreeTraverser[Defn.Object] {

  def traverse(objectDef: Defn.Object): Unit = {
    emitLine()
    emitComment("originally a Scala object")
    emitLine()
    emitTypeDeclaration(modifiers = resolveJavaClassExplicitModifiers(objectDef.mods),
      typeKeyword = "class",
      name = s"${objectDef.name.toString}")
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Class
    GenericTreeTraverser.traverse(objectDef.templ)
    javaOwnerContext = outerJavaOwnerContext
  }
}
