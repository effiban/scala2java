package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Name, Term, Type}

trait NameTraverser extends ScalaTreeTraverser[Name]

private[traversers] class NameTraverserImpl(nameIndeterminateTraverser: => NameIndeterminateTraverser,
                                            termNameRenderer: TermNameRenderer,
                                            typeNameTraverser: => TypeNameTraverser)
                                           (implicit javaWriter: JavaWriter) extends NameTraverser {

  import javaWriter._

  override def traverse(name: Name): Unit = name match {
    // Type with no explicit name, by default should be left empty in Java
    // (except special cases e.g. `this` and `super` which are handled in their traversers)
    case _: Name.Anonymous =>
    case indeterminateName: Name.Indeterminate => nameIndeterminateTraverser.traverse(indeterminateName)
    case termName: Term.Name => termNameRenderer.render(termName)
    case typeName: Type.Name => typeNameTraverser.traverse(typeName)
    case other => writeComment(s"UNSUPPORTED: $other")
  }

}
