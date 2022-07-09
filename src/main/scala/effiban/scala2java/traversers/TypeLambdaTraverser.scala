package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeLambdaTraverser extends ScalaTreeTraverser[Type.Lambda]

private[traversers] class TypeLambdaTraverserImpl(implicit javaWriter: JavaWriter) extends TypeLambdaTraverser {

  import javaWriter._

  // generic lambda type [T] => (T, T)
  // According to documentation supported only in some dialects (what does this mean?)
  override def traverse(lambdaType: Type.Lambda): Unit = {
    //TODO maybe convert simple case to Java
    writeComment(lambdaType.toString())
  }
}

object TypeLambdaTraverser extends TypeLambdaTraverserImpl
