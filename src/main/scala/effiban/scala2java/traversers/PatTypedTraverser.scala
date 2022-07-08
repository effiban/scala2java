package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Pat

trait PatTypedTraverser extends ScalaTreeTraverser[Pat.Typed]

private[scala2java] class PatTypedTraverserImpl(typeTraverser: => TypeTraverser,
                                                patTraverser: => PatTraverser)
                                               (implicit javaEmitter: JavaEmitter) extends PatTypedTraverser {

  import javaEmitter._

  // Typed pattern expression, e.g. a: Int (in lhs of case clause)
  override def traverse(typedPattern: Pat.Typed): Unit = {
    typeTraverser.traverse(typedPattern.rhs)
    emit(" ")
    patTraverser.traverse(typedPattern.lhs)
  }
}

object PatTypedTraverser extends PatTypedTraverserImpl(TypeTraverser, PatTraverser)
