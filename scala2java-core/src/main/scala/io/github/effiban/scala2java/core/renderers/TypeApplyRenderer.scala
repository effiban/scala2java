package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.SquareBracket
import io.github.effiban.scala2java.core.entities.TypeNameValues.ScalaArray
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeApplyRenderer extends JavaTreeRenderer[Type.Apply]

private[renderers] class TypeApplyRendererImpl(typeRenderer: => TypeRenderer,
                                               typeListRenderer: => TypeListRenderer)
                                              (implicit javaWriter: JavaWriter)
  extends TypeApplyRenderer {

  import javaWriter._

  // type definition with generic args, e.g. F[T]
  override def render(typeApply: Type.Apply): Unit = {
    typeApply.tpe match {
      case Type.Name(ScalaArray) => renderArrayType(typeApply.args)
      case _ =>
        typeRenderer.render(typeApply.tpe)
        typeListRenderer.render(typeApply.args)
    }
  }

  private def renderArrayType(args: List[Type]): Unit = {
    args match {
      case Nil => throw new IllegalStateException(s"A Type.Apply must have at least one type argument")
      case arg :: Nil =>
        typeRenderer.render(arg)
        writeStartDelimiter(SquareBracket)
        writeEndDelimiter(SquareBracket)
      case _ => throw new IllegalStateException(s"An Array type must have one type argument, but ${args.length} found")
    }
  }
}
