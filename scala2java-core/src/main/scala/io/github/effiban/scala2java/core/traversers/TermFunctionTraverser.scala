package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, StatContext}
import io.github.effiban.scala2java.core.entities.Decision.{Decision, Uncertain}
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.entities.JavaScope

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
    val paramContext = StatContext(JavaScope.LambdaSignature)
    function.params match {
      case param :: Nil => termParamTraverser.traverse(termParam = param, context = paramContext)
      case _ => termParamListTraverser.traverse(termParams = function.params, context = paramContext, onSameLine = true)
    }
    writeArrow()
    function.body match {
      // Block of size 2 or more
      case block@Block(_ :: _ :: _) => blockTraverser.traverse(stat = block, context = BlockContext(shouldReturnValue = shouldBodyReturnValue))
      // Block of size 1 - treat as a plain stat, because the Java style of lambdas is the same as Scala
      case Block(stat :: Nil) => statTraverser.traverse(stat)
      case stat => statTraverser.traverse(stat)
    }
  }
}
