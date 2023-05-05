package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pat

trait PatTypedTraverser extends ScalaTreeTraverser[Pat.Typed]

private[traversers] class PatTypedTraverserImpl(typeTraverser: => TypeTraverser,
                                                typeRenderer: => TypeRenderer,
                                                patTraverser: => PatTraverser)
                                               (implicit javaWriter: JavaWriter) extends PatTypedTraverser {

  import javaWriter._

  // Typed pattern expression, e.g. a: Int (in lhs of case clause)
  override def traverse(typedPattern: Pat.Typed): Unit = {
    val traversedRhs = typeTraverser.traverse(typedPattern.rhs)
    typeRenderer.render(traversedRhs)
    write(" ")
    patTraverser.traverse(typedPattern.lhs)
  }
}
