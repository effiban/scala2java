package effiban.scala2java

import scala.meta.Pat

trait PatTupleTraverser extends ScalaTreeTraverser[Pat.Tuple]

private[scala2java] class PatTupleTraverserImpl(implicit javaEmitter: JavaEmitter) extends PatTupleTraverser {
  import javaEmitter._

  // Pattern match tuple expression, no Java equivalent
  override def traverse(patternTuple: Pat.Tuple): Unit = {
    // TODO consider rewriting as a Java collection (depends on corresponding rewrite of the rest of the pattern-match clause)
    emitComment(s"(${patternTuple.args.mkString(", ")})")
  }
}

object PatTupleTraverser extends PatTupleTraverserImpl()
