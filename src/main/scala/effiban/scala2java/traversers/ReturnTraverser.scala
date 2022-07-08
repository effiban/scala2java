package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Term.Return

trait ReturnTraverser extends ScalaTreeTraverser[Return]

private[scala2java] class ReturnTraverserImpl(termTraverser: => TermTraverser)
                                             (implicit javaEmitter: JavaEmitter) extends ReturnTraverser {

  import javaEmitter._

  override def traverse(`return`: Return): Unit = {
    emit("return ")
    termTraverser.traverse(`return`.expr)
  }
}

object ReturnTraverser extends ReturnTraverserImpl(TermTraverser)
