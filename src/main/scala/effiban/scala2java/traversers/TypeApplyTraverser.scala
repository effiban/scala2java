package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.SquareBracket
import effiban.scala2java.entities.TypeNameValues
import effiban.scala2java.writers.JavaWriter

import scala.meta.Type

trait TypeApplyTraverser extends ScalaTreeTraverser[Type.Apply]

private[traversers] class TypeApplyTraverserImpl(typeTraverser: => TypeTraverser,
                                                 typeListTraverser: => TypeListTraverser)
                                                (implicit javaWriter: JavaWriter)
  extends TypeApplyTraverser {

  import javaWriter._

  // type definition with generic args, e.g. F[T]
  override def traverse(typeApply: Type.Apply): Unit = {
    typeApply.tpe match {
      case Type.Name(TypeNameValues.ScalaArray) => traverseArrayType(typeApply.args)
      case _ =>
        typeTraverser.traverse(typeApply.tpe)
        typeListTraverser.traverse(typeApply.args)
    }
  }

  private def traverseArrayType(args: List[Type]): Unit = {
    args match {
      case Nil => throw new IllegalStateException(s"A Type.Apply must have at least one type argument")
      case arg :: Nil =>
        typeTraverser.traverse(arg)
        writeStartDelimiter(SquareBracket)
        writeEndDelimiter(SquareBracket)
      case _ => throw new IllegalStateException(s"An Array type must have one type argument, but ${args.length} found")
    }
  }
}
