package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.transformers.ScalaToJavaTermApplyTransformer

import scala.meta.Term

trait TermApplyTraverser extends ScalaTreeTraverser[Term.Apply]

private[traversers] class TermApplyTraverserImpl(termTraverser: => TermTraverser,
                                                 termListTraverser: => TermListTraverser,
                                                 scalaToJavaTermApplyTransformer: ScalaToJavaTermApplyTransformer) extends TermApplyTraverser {

  // method invocation
  override def traverse(termApply: Term.Apply): Unit = {
    val javaTermApply = scalaToJavaTermApplyTransformer.transform(termApply)
    termTraverser.traverse(javaTermApply.fun)
    termListTraverser.traverse(javaTermApply.args, maybeEnclosingDelimiter = Some(Parentheses))
  }
}
