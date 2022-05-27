package com.effiban.scala2java

import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Defn

trait ObjectTraverser extends ScalaTreeTraverser[Defn.Object]

private[scala2java] class ObjectTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                              templateTraverser: => TemplateTraverser,
                                              javaModifiersResolver: JavaModifiersResolver)
                                             (implicit javaEmitter: JavaEmitter) extends ObjectTraverser {

  import javaEmitter._

  override def traverse(objectDef: Defn.Object): Unit = {
    emitLine()
    emitComment("originally a Scala object")
    emitLine()
    annotListTraverser.traverseMods(objectDef.mods)
    emitTypeDeclaration(modifiers = javaModifiersResolver.resolveForClass(objectDef.mods),
      typeKeyword = "class",
      name = s"${objectDef.name.toString}")
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Class
    templateTraverser.traverse(objectDef.templ)
    javaOwnerContext = outerJavaOwnerContext
  }
}

object ObjectTraverser extends ObjectTraverserImpl(
  AnnotListTraverser,
  TemplateTraverser,
  JavaModifiersResolver)

