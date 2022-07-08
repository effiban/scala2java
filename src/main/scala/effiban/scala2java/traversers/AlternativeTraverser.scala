package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Pat.Alternative

trait AlternativeTraverser extends ScalaTreeTraverser[Alternative]

private[scala2java] class AlternativeTraverserImpl(patTraverser: => PatTraverser)
                                                  (implicit javaWriter: JavaWriter) extends AlternativeTraverser {
  import javaWriter._

  // Pattern match alternative, e.g. 2 | 3. In Java - separated by comma
  override def traverse(patternAlternative: Alternative): Unit = {
    patTraverser.traverse(patternAlternative.lhs)
    write(", ")
    patTraverser.traverse(patternAlternative.rhs)
  }
}

object AlternativeTraverser extends AlternativeTraverserImpl(PatTraverser)
