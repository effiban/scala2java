package effiban.scala2java.traversers

import effiban.scala2java.contexts.CatchHandlerContext
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait CatchHandlerTraverser {
  def traverse(param: Term.Param,
               body: Term,
               context: CatchHandlerContext = CatchHandlerContext()): Unit
}

private[traversers] class CatchHandlerTraverserImpl(termParamListTraverser: => TermParamListTraverser,
                                                    blockTraverser: => BlockTraverser)
                                                   (implicit javaWriter: JavaWriter) extends CatchHandlerTraverser {

  import javaWriter._

  override def traverse(param: Term.Param,
                        body: Term,
                        context: CatchHandlerContext = CatchHandlerContext()): Unit = {
    write("catch ")
    termParamListTraverser.traverse(termParams = List(param), onSameLine = true)
    blockTraverser.traverse(body, shouldReturnValue = context.shouldReturnValue)
  }
}
