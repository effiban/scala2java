package effiban.scala2java.traversers

import effiban.scala2java.transformers.ScalaToJavaTypeNameTransformer
import effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeNameTraverser extends ScalaTreeTraverser[Type.Name]

private[scala2java] class TypeNameTraverserImpl(scalaToJavaTypeNameTransformer: ScalaToJavaTypeNameTransformer)
                                               (implicit javaWriter: JavaWriter) extends TypeNameTraverser {

  import javaWriter._

  override def traverse(name: Type.Name): Unit = {
    write(scalaToJavaTypeNameTransformer.transform(name))
  }
}

object TypeNameTraverser extends TypeNameTraverserImpl(ScalaToJavaTypeNameTransformer)