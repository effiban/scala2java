package effiban.scala2java.traversers

import effiban.scala2java.contexts.{BlockContext, StatContext}
import effiban.scala2java.entities.Decision.{Decision, Uncertain}
import effiban.scala2java.entities.JavaTreeType.Lambda
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.Block

trait TermFunctionTraverser {
  def traverse(function: Term.Function, shouldBodyReturnValue: Decision = Uncertain): Unit
}

private[traversers] class TermFunctionTraverserImpl(termParamTraverser: => TermParamTraverser,
                                                    termParamListTraverser: => TermParamListTraverser,
                                                    statTraverser: => StatTraverser,
                                                    blockTraverser: => BlockTraverser)
                                                   (implicit javaWriter: JavaWriter) extends TermFunctionTraverser {

  import javaWriter._

  // lambda definition
  override def traverse(function: Term.Function, shouldBodyReturnValue: Decision = Uncertain): Unit = {
    val outerJavaScope = javaScope
    javaScope = Lambda
    function.params match {
      case param :: Nil => termParamTraverser.traverse(param)
      case _ => termParamListTraverser.traverse(termParams = function.params, context = StatContext(Lambda), onSameLine = true)
    }
    writeArrow()
    function.body match {
      // Block of size 2 or more
      case block@Block(_ :: _ :: _) => blockTraverser.traverse(stat = block, context = BlockContext(shouldReturnValue = shouldBodyReturnValue))
      // Block of size 1 - treat as a plain stat, because the Java style of lambdas is the same as Scala
      case Block(stat :: Nil) => statTraverser.traverse(stat)
      case stat => statTraverser.traverse(stat)
    }
    javaScope = outerJavaScope
  }
}
