package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait FinallyTraverser extends ScalaTreeTraverser[Term]

private[traversers] class FinallyTraverserImpl(blockTraverser: => BlockTraverser)
                                              (implicit javaWriter: JavaWriter) extends FinallyTraverser {

  import javaWriter._

  // TODO support return value flag
  override def traverse(finallyp: Term): Unit = {
    write("finally")
    blockTraverser.traverse(finallyp)
  }
}
