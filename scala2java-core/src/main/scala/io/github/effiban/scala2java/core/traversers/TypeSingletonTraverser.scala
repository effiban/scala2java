package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeSingletonRenderer

import scala.meta.{Term, Type}

trait TypeSingletonTraverser extends ScalaTreeTraverser[Type.Singleton]

private[traversers] class TypeSingletonTraverserImpl(thisTraverser: ThisTraverser,
                                                     typeSingletonRenderer: TypeSingletonRenderer) extends TypeSingletonTraverser {

  // A special Scala type representing the unique type of a term, e.g.: a.type where 'a' is some variable
  // Unsupported in Java, except for the special case of 'this' which is the type used for a ctor. invocation inside a secondary ctor.
  override def traverse(singletonType: Type.Singleton): Unit = {
    val traversedSingletonType = singletonType.ref match {
      case `this`: Term.This => singletonType.copy(ref = thisTraverser.traverse(`this`))
      case _ => singletonType
    }
    typeSingletonRenderer.render(traversedSingletonType)
  }
}
