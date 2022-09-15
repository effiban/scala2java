package effiban.scala2java.traversers

import effiban.scala2java.transformers.ScalaToJavaFunctionTypeTransformer
import effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeFunctionTraverser extends ScalaTreeTraverser[Type.Function]

private[traversers] class TypeFunctionTraverserImpl(typeApplyTraverser: => TypeApplyTraverser,
                                                    scalaToJavaFunctionTypeTransformer: ScalaToJavaFunctionTypeTransformer)
                                                   (implicit javaWriter: JavaWriter)
  extends TypeFunctionTraverser {

  private final val JoolFunctionTypeRegex = "Function\\d".r

  import javaWriter._

  // function type, e.g.: Int => String
  override def traverse(functionType: Type.Function): Unit = {
    val javaFunctionType = scalaToJavaFunctionTypeTransformer.transform(functionType)
    javaFunctionType.tpe match {
      case tpe@Type.Name(name) if JoolFunctionTypeRegex.matches(name) =>
        // TODO add this to README, and maybe add import automatically once we can
        writeComment(s"Requires JOOL (import org.jooq.lambda.function.${tpe.value})")
      case _ =>
    }
    typeApplyTraverser.traverse(javaFunctionType)
  }
}
