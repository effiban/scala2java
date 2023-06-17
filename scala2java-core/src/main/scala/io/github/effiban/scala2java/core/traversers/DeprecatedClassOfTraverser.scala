package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

// A traverser for the special expression 'classOf[T]'
@deprecated
trait DeprecatedClassOfTraverser extends ScalaTreeTraverser1[Term.ApplyType]

@deprecated
private[traversers] class DeprecatedClassOfTraverserImpl(typeTraverser: => TypeTraverser) extends DeprecatedClassOfTraverser {

  override def traverse(classOfType: Term.ApplyType): Term.ApplyType = {
    val traversedTargs = classOfType.targs.map(typeTraverser.traverse)
    classOfType.copy(targs = traversedTargs)
  }
}