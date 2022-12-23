package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.transformers.TypeNameTransformer

import scala.meta.Type

trait TypeNameTraverser extends ScalaTreeTraverser[Type.Name]

private[traversers] class TypeNameTraverserImpl(typeNameTransformer: TypeNameTransformer)
                                               (implicit javaWriter: JavaWriter) extends TypeNameTraverser {

  import javaWriter._

  override def traverse(name: Type.Name): Unit = {
    write(typeNameTransformer.transform(name).value)
  }
}
