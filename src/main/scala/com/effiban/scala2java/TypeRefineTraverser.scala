package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Type

trait TypeRefineTraverser extends ScalaTreeTraverser[Type.Refine]

private[scala2java] class TypeRefineTraverserImpl(typeTraverser: => TypeTraverser)
                                                 (implicit javaEmitter: JavaEmitter) extends TypeRefineTraverser {

  // A {def f: Int }
  override def traverse(refinedType: Type.Refine): Unit = {
    refinedType.tpe.foreach(typeTraverser.traverse)
    //TODO maybe convert to Java type with inheritance
    emitComment(s" ${refinedType.stats.toString()}")
  }
}

object TypeRefineTraverser extends TypeRefineTraverserImpl(TypeTraverser)