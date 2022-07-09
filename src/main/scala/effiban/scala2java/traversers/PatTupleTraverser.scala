package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Pat

trait PatTupleTraverser extends ScalaTreeTraverser[Pat.Tuple]

private[traversers] class PatTupleTraverserImpl(implicit javaWriter: JavaWriter) extends PatTupleTraverser {

  import javaWriter._

  // Pattern match tuple expression, no Java equivalent
  override def traverse(patternTuple: Pat.Tuple): Unit = {
    // TODO consider rewriting as a Java collection (depends on corresponding rewrite of the rest of the pattern-match clause)
    writeComment(s"(${patternTuple.args.mkString(", ")})")
  }
}
