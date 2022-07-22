package effiban.scala2java.traversers

import effiban.scala2java.entities.TraversalConstants.JavaPlaceholder
import effiban.scala2java.writers.JavaWriter

import scala.meta.Pat

trait PatWildcardTraverser extends ScalaTreeTraverser[Pat.Wildcard]

private[traversers] class PatWildcardTraverserImpl(implicit javaWriter: JavaWriter) extends PatWildcardTraverser {

  import javaWriter._

  // Wildcard in pattern match expression - translates to Java placeholder (but not always supported)
  // When used alone it should be translated to "default" and this is handled by the parent traverser (CaseTraverser)
  override def traverse(ignored: Pat.Wildcard): Unit = write(JavaPlaceholder)
}
