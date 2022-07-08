package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Type

trait TypeLambdaTraverser extends ScalaTreeTraverser[Type.Lambda]

private[scala2java] class TypeLambdaTraverserImpl(implicit javaEmitter: JavaEmitter) extends TypeLambdaTraverser {

  import javaEmitter._

  // generic lambda type [T] => (T, T)
  // According to documentation supported only in some dialects (what does this mean?)
  override def traverse(lambdaType: Type.Lambda): Unit = {
    //TODO maybe convert simple case to Java
    emitComment(lambdaType.toString())
  }
}

object TypeLambdaTraverser extends TypeLambdaTraverserImpl
