package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.ForYield

trait ForYieldTraverser extends ScalaTreeTraverser[ForYield] with ForVariantTraverser {
  override val intermediateFunctionName: Term.Name = Term.Name("flatMap")
  override val finalFunctionName: Term.Name = Term.Name("map")
}

private[traversers] class ForYieldTraverserImpl(theTermTraverser: => TermTraverser)
                                          (implicit override val javaWriter: JavaWriter) extends ForYieldTraverser {
  override def termTraverser: TermTraverser = theTermTraverser

  override def traverse(forYield: ForYield): Unit = {
    traverse(forYield.enums, forYield.body)
  }
}
