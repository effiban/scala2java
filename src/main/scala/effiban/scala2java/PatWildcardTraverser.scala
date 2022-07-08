package effiban.scala2java

import scala.meta.Pat

trait PatWildcardTraverser extends ScalaTreeTraverser[Pat.Wildcard]

private[scala2java] class PatWildcardTraverserImpl(implicit javaEmitter: JavaEmitter) extends PatWildcardTraverser {

  import javaEmitter._

  // Wildcard in pattern match expression - translates to Java "default" ?
  override def traverse(ignored: Pat.Wildcard): Unit = emit("default")
}

object PatWildcardTraverser extends PatWildcardTraverserImpl()
