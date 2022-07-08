package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeRefineTraverser extends ScalaTreeTraverser[Type.Refine]

private[scala2java] class TypeRefineTraverserImpl(typeTraverser: => TypeTraverser)
                                                 (implicit javaWriter: JavaWriter) extends TypeRefineTraverser {

  import javaWriter._

  // Scala feature which allows to extend the definition of a type, e.g. the block in the RHS below:
  // type B = A {def f: Int}
  override def traverse(refinedType: Type.Refine): Unit = {
    refinedType.tpe.foreach(typeTraverser.traverse)
    //TODO maybe convert to Java type with inheritance
    writeComment(s"${refinedType.stats.toString()}")
  }
}

object TypeRefineTraverser extends TypeRefineTraverserImpl(TypeTraverser)