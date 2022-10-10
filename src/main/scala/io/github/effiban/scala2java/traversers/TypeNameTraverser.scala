package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.transformers.TypeNameTransformer
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeNameTraverser extends ScalaTreeTraverser[Type.Name]

private[traversers] class TypeNameTraverserImpl(typeNameTransformer: TypeNameTransformer)
                                               (implicit javaWriter: JavaWriter) extends TypeNameTraverser {

  import javaWriter._

  override def traverse(name: Type.Name): Unit = {
    write(typeNameTransformer.transform(name))
  }
}