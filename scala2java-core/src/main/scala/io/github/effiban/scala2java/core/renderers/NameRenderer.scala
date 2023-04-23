package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Name, Term, Type}

trait NameRenderer extends JavaTreeRenderer[Name]

private[renderers] class NameRendererImpl(nameIndeterminateRenderer: NameIndeterminateRenderer,
                                          termNameRenderer: TermNameRenderer,
                                          typeNameRenderer: TypeNameRenderer)
                                         (implicit javaWriter: JavaWriter) extends NameRenderer {

  import javaWriter._

  override def render(name: Name): Unit = name match {
    // Type with no explicit name, by default should be left empty in Java
    // (except special cases e.g. `this` and `super` which are handled in their traversers)
    case _: Name.Anonymous =>
    case indeterminateName: Name.Indeterminate => nameIndeterminateRenderer.render(indeterminateName)
    case termName: Term.Name => termNameRenderer.render(termName)
    case typeName: Type.Name => typeNameRenderer.render(typeName)
    case other => writeComment(s"UNSUPPORTED: $other")
  }

}
