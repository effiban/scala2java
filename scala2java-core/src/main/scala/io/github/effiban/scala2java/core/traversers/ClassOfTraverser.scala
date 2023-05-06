package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.ClassOfRenderer

import scala.meta.Term

// A traverser for the special expression 'classOf[T]'
trait ClassOfTraverser {
  def traverse(classOf: Term.ApplyType): Unit
}

private[traversers] class ClassOfTraverserImpl(typeTraverser: => TypeTraverser,
                                               classOfRenderer: => ClassOfRenderer) extends ClassOfTraverser {

  override def traverse(classOfType: Term.ApplyType): Unit = {
    val traversedTargs = classOfType.targs.map(typeTraverser.traverse)
    val traversedClassOfType = classOfType.copy(targs = traversedTargs)
    classOfRenderer.render(traversedClassOfType)
  }
}