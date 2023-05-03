package io.github.effiban.scala2java.core.traversers

import scala.meta.{Term, Type}

trait TypeSingletonTraverser extends ScalaTreeTraverser1[Type.Singleton]

private[traversers] class TypeSingletonTraverserImpl(thisTraverser: ThisTraverser) extends TypeSingletonTraverser {

  // A special Scala type representing the unique type of a term, e.g.: a.type where 'a' is some variable
  // Unsupported in Java, except for the special case of 'this' which is the type used for a ctor. invocation inside a secondary ctor.
  override def traverse(singletonType: Type.Singleton): Type.Singleton = {
    singletonType.ref match {
      case `this`: Term.This => singletonType.copy(ref = thisTraverser.traverse(`this`))
      case _ => singletonType
    }
  }
}
