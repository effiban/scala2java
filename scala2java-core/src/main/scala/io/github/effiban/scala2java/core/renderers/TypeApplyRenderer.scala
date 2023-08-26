package io.github.effiban.scala2java.core.renderers

import scala.meta.{Type, XtensionQuasiquoteType}

trait TypeApplyRenderer extends JavaTreeRenderer[Type.Apply]

private[renderers] class TypeApplyRendererImpl(typeRenderer: => TypeRenderer,
                                               typeListRenderer: => TypeListRenderer,
                                               arrayTypeRenderer: => ArrayTypeRenderer) extends TypeApplyRenderer {

  // type definition with generic args, e.g. F[T]
  override def render(typeApply: Type.Apply): Unit = {
    typeApply.tpe match {
      case t"scala.Array" => renderArrayType(typeApply.args)
      case _ =>
        typeRenderer.render(typeApply.tpe)
        typeListRenderer.render(typeApply.args)
    }
  }

  private def renderArrayType(types: List[Type]): Unit = {
    types match {
      case Nil => throw new IllegalStateException(s"A Type.Apply must have at least one type argument")
      case tpe :: Nil => arrayTypeRenderer.render(tpe)
      case _ => throw new IllegalStateException(s"An Array type must have one type argument, but ${types.length} found")
    }
  }
}
