package effiban.scala2java.traversers

import effiban.scala2java.transformers.ScalaToJavaFunctionTypeTransformer
import effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeFunctionTraverser extends ScalaTreeTraverser[Type.Function]

private[traversers] class TypeFunctionTraverserImpl(typeApplyTraverser: => TypeApplyTraverser,
                                                    scalaToJavaFunctionTypeTransformer: ScalaToJavaFunctionTypeTransformer)
                                                   (implicit javaWriter: JavaWriter)
  extends TypeFunctionTraverser {

  import javaWriter._

  // function type, e.g.: Int => String
  override def traverse(functionType: Type.Function): Unit = {
    scalaToJavaFunctionTypeTransformer.transform(functionType) match {
      case Some(javaFunctionType) => typeApplyTraverser.traverse(javaFunctionType)
      case None => writeComment(functionType.toString())
    }
  }
}

object TypeFunctionTraverser extends TypeFunctionTraverserImpl(TypeApplyTraverser, ScalaToJavaFunctionTypeTransformer)