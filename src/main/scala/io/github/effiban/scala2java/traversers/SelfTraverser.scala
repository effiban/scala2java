package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Self

trait SelfTraverser extends ScalaTreeTraverser[Self]

private[traversers] class SelfTraverserImpl(implicit javaWriter: JavaWriter) extends SelfTraverser {

  import javaWriter._

  override def traverse(`self`: Self): Unit = {
    self.decltpe.foreach(_ => {
      //TODO - consider translating the 'self' type into a Java parent
      writeComment(s"extends ${self.toString()}")
    })
  }
}
