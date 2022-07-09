package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Pat
import scala.meta.Pat.Bind

trait BindTraverser extends ScalaTreeTraverser[Pat.Bind]

private[traversers] class BindTraverserImpl(patTraverser: => PatTraverser)
                                           (implicit javaWriter: JavaWriter) extends BindTraverser {

  import javaWriter._

  // Pattern match bind variable, e.g.: a @ A().
  override def traverse(patternBind: Bind): Unit = {
    //TODO - consider supporting in Java by converting to a guard?
    writeComment(patternBind.toString())
  }
}
