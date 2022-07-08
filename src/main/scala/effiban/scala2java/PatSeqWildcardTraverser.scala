package effiban.scala2java

import scala.meta.Pat

trait PatSeqWildcardTraverser extends ScalaTreeTraverser[Pat.SeqWildcard]

class PatSeqWildcardTraverserImpl(implicit javaEmitter: JavaEmitter) extends PatSeqWildcardTraverser {
  import javaEmitter._

  // Vararg in pattern match expression, e.g. `_*` in case List(xs @ _*).
  override def traverse(patternSeqWildcard: Pat.SeqWildcard): Unit = {
    //TODO consider transforming to Java guard
    emitComment("...")
  }
}

object PatSeqWildcardTraverser extends PatSeqWildcardTraverserImpl()
