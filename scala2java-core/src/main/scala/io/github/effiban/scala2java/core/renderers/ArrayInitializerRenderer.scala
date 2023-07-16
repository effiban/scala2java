package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.{CurlyBrace, SquareBracket}
import io.github.effiban.scala2java.core.entities.JavaKeyword.New
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.renderers.contexts.{ArrayInitializerSizeRenderContext, ArrayInitializerValuesRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Term, Type}

trait ArrayInitializerRenderer {
  def renderWithValues(context: ArrayInitializerValuesRenderContext): Unit

  def renderWithSize(context: ArrayInitializerSizeRenderContext): Unit
}

private[renderers] class ArrayInitializerRendererImpl(typeRenderer: => TypeRenderer,
                                                      expressionTermRenderer: => ExpressionTermRenderer,
                                                      argumentRenderer: => ArgumentRenderer[Term],
                                                      argumentListRenderer: => ArgumentListRenderer)
                                                     (implicit javaWriter: JavaWriter) extends ArrayInitializerRenderer {

  import javaWriter._

  override def renderWithValues(context: ArrayInitializerValuesRenderContext): Unit = {
    import context._

    renderNewAndType(tpe)
    writeStartDelimiter(SquareBracket)
    writeEndDelimiter(SquareBracket)
    write(" ")
    val options = ListTraversalOptions(maybeEnclosingDelimiter = Some(CurlyBrace), traverseEmpty = true)
    argumentListRenderer.render(
      args = values,
      argRenderer = argumentRenderer,
      context = ArgumentListContext(options = options))
  }

  override def renderWithSize(context: ArrayInitializerSizeRenderContext): Unit = {
    import context._

    renderNewAndType(tpe)
    writeStartDelimiter(SquareBracket)
    expressionTermRenderer.render(size)
    writeEndDelimiter(SquareBracket)
  }

  private def renderNewAndType(tpe: Type): Unit = {
    writeKeyword(New)
    write(" ")
    typeRenderer.render(tpe)
  }
}
