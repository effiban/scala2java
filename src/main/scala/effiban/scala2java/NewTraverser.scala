package effiban.scala2java

import scala.meta.Term.New

trait NewTraverser extends ScalaTreeTraverser[New]

private[scala2java] class NewTraverserImpl(initTraverser: => InitTraverser)
                                          (implicit javaEmitter: JavaEmitter) extends NewTraverser {

  import javaEmitter._

  override def traverse(`new`: New): Unit = {
    emit("new ")
    initTraverser.traverse(`new`.init)
  }
}

object NewTraverser extends NewTraverserImpl(InitTraverser)
