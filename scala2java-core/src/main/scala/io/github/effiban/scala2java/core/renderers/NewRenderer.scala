package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.InitContext
import io.github.effiban.scala2java.core.entities.JavaKeyword
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerRenderContextResolver
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.New

trait NewRenderer extends JavaTreeRenderer[New]

private[renderers] class NewRendererImpl(initRenderer: => InitRenderer,
                                         arrayInitializerRenderer: => ArrayInitializerRenderer,
                                         arrayInitializerRenderContextResolver: ArrayInitializerRenderContextResolver)
                                        (implicit javaWriter: JavaWriter) extends NewRenderer {

  import javaWriter._

  override def render(`new`: New): Unit = {
    arrayInitializerRenderContextResolver.tryResolve(`new`.init) match {
      case Some(context) => arrayInitializerRenderer.renderWithSize(context)
      case None => renderRegular(`new`)
    }
  }

  private def renderRegular(`new`: New): Unit = {
    writeKeyword(JavaKeyword.New)
    write(" ")
    initRenderer.render(`new`.init, InitContext(traverseEmpty = true, argNameAsComment = true))
  }
}
