package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Pat

trait PatWildcardTraverser extends ScalaTreeTraverser[Pat.Wildcard]

private[scala2java] class PatWildcardTraverserImpl(implicit javaWriter: JavaWriter) extends PatWildcardTraverser {

  import javaWriter._

  // Wildcard in pattern match expression - translates to Java "default" ?
  override def traverse(ignored: Pat.Wildcard): Unit = write("default")
}

object PatWildcardTraverser extends PatWildcardTraverserImpl()
