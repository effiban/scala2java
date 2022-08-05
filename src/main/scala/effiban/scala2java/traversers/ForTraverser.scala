package effiban.scala2java.traversers

import effiban.scala2java.transformers.PatToTermParamTransformer
import effiban.scala2java.traversers.ForTraverser.ForEachFunctionName
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.For

trait ForTraverser extends ScalaTreeTraverser[For] with ForVariantTraverser {
  override val intermediateFunctionName: Term.Name = ForEachFunctionName
  override val finalFunctionName: Term.Name = ForEachFunctionName
}

private[traversers] class ForTraverserImpl(theTermTraverser: => TermTraverser,
                                           thePatToTermParamTransformer: PatToTermParamTransformer)
                                          (implicit override val javaWriter: JavaWriter) extends ForTraverser {
  override def termTraverser: TermTraverser = theTermTraverser
  override def patToTermParamTransformer: PatToTermParamTransformer = thePatToTermParamTransformer

  override def traverse(`for`: For): Unit = {
    traverse(`for`.enums, `for`.body)
  }
}

private[traversers] object ForTraverser {
  final val ForEachFunctionName: Term.Name = Term.Name("forEach")
}