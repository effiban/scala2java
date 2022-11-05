package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeSelectTraverser extends ScalaTreeTraverser[Type.Select]

private[traversers] class TypeSelectTraverserImpl(termRefTraverser: => TermRefTraverser,
                                                  typeNameTraverser: => TypeNameTraverser)
                                                 (implicit javaWriter: JavaWriter) extends TypeSelectTraverser {

  import javaWriter._

  // A scala type selecting a type from a term, e.g.: a.B where 'a' is of some class 'A' that has a 'B' type inside.
  // I think it's supported in Java, but will produce a warning that a 'static member is being accessed from a non-static context'.
  override def traverse(typeSelect: Type.Select): Unit = {
    termRefTraverser.traverse(typeSelect.qual)
    writeQualifierSeparator()
    typeNameTraverser.traverse(typeSelect.name)
  }
}
