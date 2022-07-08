package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter
import effiban.scala2java.entities.Lambda
import effiban.scala2java.entities.TraversalContext.javaOwnerContext

import scala.meta.Term

trait TermFunctionTraverser extends ScalaTreeTraverser[Term.Function]

private[scala2java] class TermFunctionTraverserImpl(termParamTraverser: => TermParamTraverser,
                                                    termParamListTraverser: => TermParamListTraverser,
                                                    termTraverser: => TermTraverser)
                                                   (implicit javaEmitter: JavaEmitter) extends TermFunctionTraverser {

  import javaEmitter._

  // lambda definition
  override def traverse(function: Term.Function): Unit = {
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Lambda
    function.params match {
      case param :: Nil => termParamTraverser.traverse(param)
      case _ => termParamListTraverser.traverse(termParams = function.params, onSameLine = true)
    }
    emitArrow()
    termTraverser.traverse(function.body)
    javaOwnerContext = outerJavaOwnerContext
  }
}

object TermFunctionTraverser extends TermFunctionTraverserImpl(
  TermParamTraverser,
  TermParamListTraverser,
  TermTraverser
)
