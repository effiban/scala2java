package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

// A traverser for the special expression 'classOf[T]'
trait ClassOfTraverser extends ScalaTreeTraverser1[Term.ApplyType]

private[traversers] class ClassOfTraverserImpl(typeTraverser: => TypeTraverser) extends ClassOfTraverser {

  override def traverse(classOfType: Term.ApplyType): Term.ApplyType = {
    val traversedTargs = classOfType.targs.map(typeTraverser.traverse)
    classOfType.copy(targs = traversedTargs)
  }
}