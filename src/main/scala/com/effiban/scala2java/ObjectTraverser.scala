package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emitComment, emitLine, emitTypeDeclaration}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Defn

trait ObjectTraverser extends ScalaTreeTraverser[Defn.Object]

private[scala2java] class ObjectTraverserImpl(templateTraverser: => TemplateTraverser,
                                              javaModifiersResolver: JavaModifiersResolver) extends ObjectTraverser {

  override def traverse(objectDef: Defn.Object): Unit = {
    emitLine()
    emitComment("originally a Scala object")
    emitLine()
    emitTypeDeclaration(modifiers = javaModifiersResolver.resolveForClass(objectDef.mods),
      typeKeyword = "class",
      name = s"${objectDef.name.toString}")
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Class
    templateTraverser.traverse(objectDef.templ)
    javaOwnerContext = outerJavaOwnerContext
  }
}

object ObjectTraverser extends ObjectTraverserImpl(TemplateTraverser, JavaModifiersResolver)

