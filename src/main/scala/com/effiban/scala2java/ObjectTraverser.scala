package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emitComment, emitLine, emitTypeDeclaration}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Defn

object ObjectTraverser extends ScalaTreeTraverser[Defn.Object] {

  def traverse(objectDef: Defn.Object): Unit = {
    emitLine()
    emitComment("originally a Scala object")
    emitLine()
    emitTypeDeclaration(modifiers = JavaModifiersResolver.resolveForClass(objectDef.mods),
      typeKeyword = "class",
      name = s"${objectDef.name.toString}")
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Class
    TemplateTraverser.traverse(objectDef.templ)
    javaOwnerContext = outerJavaOwnerContext
  }
}
