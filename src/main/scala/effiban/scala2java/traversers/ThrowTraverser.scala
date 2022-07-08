package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Term.Throw

trait ThrowTraverser extends ScalaTreeTraverser[Throw]

private[scala2java] class ThrowTraverserImpl(termTraverser: => TermTraverser)
                                            (implicit javaEmitter: JavaEmitter) extends ThrowTraverser {

  import javaEmitter._

  override def traverse(`throw`: Throw): Unit = {
    emit("throw ")
    termTraverser.traverse(`throw`.expr)
  }
}

object ThrowTraverser extends ThrowTraverserImpl(TermTraverser)
