package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter
import effiban.scala2java.transformers.ScalaToJavaTypeNameTransformer

import scala.meta.Type

trait TypeNameTraverser extends ScalaTreeTraverser[Type.Name]

private[scala2java] class TypeNameTraverserImpl(scalaToJavaTypeNameTransformer: ScalaToJavaTypeNameTransformer)
                                               (implicit javaEmitter: JavaEmitter) extends TypeNameTraverser {

  import javaEmitter._

  override def traverse(name: Type.Name): Unit = {
    emit(scalaToJavaTypeNameTransformer.transform(name))
  }
}

object TypeNameTraverser extends TypeNameTraverserImpl(ScalaToJavaTypeNameTransformer)