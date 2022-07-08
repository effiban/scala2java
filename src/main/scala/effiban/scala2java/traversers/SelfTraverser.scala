package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Self

trait SelfTraverser extends ScalaTreeTraverser[Self]

private[scala2java] class SelfTraverserImpl(implicit javaEmitter: JavaEmitter) extends SelfTraverser {

  import javaEmitter._

  override def traverse(`self`: Self): Unit = {
    self.decltpe.foreach(_ => {
      //TODO - consider translating the 'self' type into a Java parent
      emitComment(s"extends ${self.toString()}")
    })
  }
}

object SelfTraverser extends SelfTraverserImpl
