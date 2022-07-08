package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeSelectTraverser extends ScalaTreeTraverser[Type.Select]

private[scala2java] class TypeSelectTraverserImpl(termRefTraverser: => TermRefTraverser,
                                                  typeNameTraverser: => TypeNameTraverser)
                                                 (implicit javaWriter: JavaWriter) extends TypeSelectTraverser {

  import javaWriter._

  // A scala type selecting expression like: a.B
  override def traverse(typeSelect: Type.Select): Unit = {
    termRefTraverser.traverse(typeSelect.qual)
    write(".")
    typeNameTraverser.traverse(typeSelect.name)
  }
}

object TypeSelectTraverser extends TypeSelectTraverserImpl(TermRefTraverser, TypeNameTraverser)