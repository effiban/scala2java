package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.Throw

trait ThrowTraverser extends ScalaTreeTraverser[Throw]

private[scala2java] class ThrowTraverserImpl(termTraverser: => TermTraverser)
                                            (implicit javaWriter: JavaWriter) extends ThrowTraverser {

  import javaWriter._

  override def traverse(`throw`: Throw): Unit = {
    write("throw ")
    termTraverser.traverse(`throw`.expr)
  }
}

object ThrowTraverser extends ThrowTraverserImpl(TermTraverser)
