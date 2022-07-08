package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Pat.Alternative

trait AlternativeTraverser extends ScalaTreeTraverser[Alternative]

private[scala2java] class AlternativeTraverserImpl(patTraverser: => PatTraverser)
                                                  (implicit javaEmitter: JavaEmitter) extends AlternativeTraverser {
  import javaEmitter._

  // Pattern match alternative, e.g. 2 | 3. In Java - separated by comma
  override def traverse(patternAlternative: Alternative): Unit = {
    patTraverser.traverse(patternAlternative.lhs)
    emit(", ")
    patTraverser.traverse(patternAlternative.rhs)
  }
}

object AlternativeTraverser extends AlternativeTraverserImpl(PatTraverser)