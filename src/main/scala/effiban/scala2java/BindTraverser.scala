package effiban.scala2java

import scala.meta.Pat
import scala.meta.Pat.Bind

trait BindTraverser extends ScalaTreeTraverser[Pat.Bind]

private[scala2java] class BindTraverserImpl(patTraverser: => PatTraverser)
                                           (implicit javaEmitter: JavaEmitter) extends BindTraverser {

  import javaEmitter._

  // Pattern match bind variable, e.g.: a @ A().
  override def traverse(patternBind: Bind): Unit = {
    //TODO - consider supporting in Java by converting to a guard?
    emitComment(patternBind.toString())
  }
}

object BindTraverser extends BindTraverserImpl(PatTraverser)
