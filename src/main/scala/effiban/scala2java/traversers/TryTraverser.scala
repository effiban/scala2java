package effiban.scala2java.traversers

import effiban.scala2java.contexts.{CatchHandlerContext, TryContext}
import effiban.scala2java.transformers.PatToTermParamTransformer
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Term, Type}

trait TryTraverser {
  def traverse(`try`: Term.Try, context: TryContext = TryContext()): Unit
}

private[traversers] class TryTraverserImpl(blockTraverser: => BlockTraverser,
                                           catchHandlerTraverser: => CatchHandlerTraverser,
                                           finallyTraverser: => FinallyTraverser,
                                           patToTermParamTransformer: PatToTermParamTransformer)
                                          (implicit javaWriter: JavaWriter) extends TryTraverser {

  import javaWriter._

  // TODO Support case condition by moving into body
  override def traverse(`try`: Term.Try, context: TryContext = TryContext()): Unit = {
    write("try")
    blockTraverser.traverse(`try`.expr, shouldReturnValue = context.shouldReturnValue)
    `try`.catchp.foreach(`case` => {
      patToTermParamTransformer.transform(`pat` = `case`.pat, maybeDefaultType = Some(Type.Name("Throwable"))) match {
        case Some(param) => catchHandlerTraverser.traverse(
          param = param,
          body = `case`.body,
          context = CatchHandlerContext(shouldReturnValue = context.shouldReturnValue))
        case None => writeComment(s"UNPARSEABLE catch clause: ${`case`}")
      }
    })
    `try`.finallyp.foreach(finallyTraverser.traverse)
  }
}
