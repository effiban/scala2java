package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaTreeType.Lambda
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait TermFunctionTraverser extends ScalaTreeTraverser[Term.Function]

private[traversers] class TermFunctionTraverserImpl(termParamTraverser: => TermParamTraverser,
                                                    termParamListTraverser: => TermParamListTraverser,
                                                    termTraverser: => TermTraverser)
                                                   (implicit javaWriter: JavaWriter) extends TermFunctionTraverser {

  import javaWriter._

  // lambda definition
  override def traverse(function: Term.Function): Unit = {
    val outerJavaScope = javaScope
    javaScope = Lambda
    function.params match {
      case param :: Nil => termParamTraverser.traverse(param)
      case _ => termParamListTraverser.traverse(termParams = function.params, onSameLine = true)
    }
    writeArrow()
    termTraverser.traverse(function.body)
    javaScope = outerJavaScope
  }
}
