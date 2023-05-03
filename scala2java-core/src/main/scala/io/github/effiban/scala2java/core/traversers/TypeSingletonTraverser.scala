package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.ThisRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Term, Type}

trait TypeSingletonTraverser extends ScalaTreeTraverser[Type.Singleton]

private[traversers] class TypeSingletonTraverserImpl(thisTraverser: ThisTraverser,
                                                     thisRenderer: ThisRenderer)
                                                    (implicit javaWriter: JavaWriter) extends TypeSingletonTraverser {

  import javaWriter._

  // A special Scala type representing the unique type of a term, e.g.: a.type where 'a' is some variable
  // Unsupported in Java, except for the special case of 'this' which is the type used for a ctor. invocation inside a secondary ctor.
  override def traverse(singletonType: Type.Singleton): Unit = {
    singletonType.ref match {
      case `this`: Term.This =>
        val traversedThis = thisTraverser.traverse(`this`)
        thisRenderer.render(traversedThis)
      case aRef => writeComment(s"$aRef.type")
    }
  }
}
