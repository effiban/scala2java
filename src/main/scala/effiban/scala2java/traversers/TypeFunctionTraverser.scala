package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter
import effiban.scala2java.transformers.ScalaToJavaFunctionTypeTransformer

import scala.meta.Type

trait TypeFunctionTraverser extends ScalaTreeTraverser[Type.Function]

private[scala2java] class TypeFunctionTraverserImpl(typeApplyTraverser: => TypeApplyTraverser,
                                                    scalaToJavaFunctionTypeTransformer: ScalaToJavaFunctionTypeTransformer)
                                                   (implicit javaEmitter: JavaEmitter)
  extends TypeFunctionTraverser {

  import javaEmitter._

  // function type, e.g.: Int => String
  override def traverse(functionType: Type.Function): Unit = {
    scalaToJavaFunctionTypeTransformer.transform(functionType) match {
      case Some(javaFunctionType) => typeApplyTraverser.traverse(javaFunctionType)
      case None => emitComment(functionType.toString())
    }
  }
}

object TypeFunctionTraverser extends TypeFunctionTraverserImpl(TypeApplyTraverser, ScalaToJavaFunctionTypeTransformer)