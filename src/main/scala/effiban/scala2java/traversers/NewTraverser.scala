package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.New

trait NewTraverser extends ScalaTreeTraverser[New]

private[traversers] class NewTraverserImpl(initTraverser: => InitTraverser)
                                          (implicit javaWriter: JavaWriter) extends NewTraverser {

  import javaWriter._

  override def traverse(`new`: New): Unit = {
    write("new ")
    initTraverser.traverse(`new`.init)
  }
}

object NewTraverser extends NewTraverserImpl(InitTraverser)
