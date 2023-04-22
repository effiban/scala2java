package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Name, Term, Type}

trait NameTraverser extends ScalaTreeTraverser[Name]

private[traversers] class NameTraverserImpl(nameAnonymousTraverser: => NameAnonymousTraverser,
                                            nameIndeterminateTraverser: => NameIndeterminateTraverser,
                                            termNameRenderer: TermNameRenderer,
                                            typeNameTraverser: => TypeNameTraverser)
                                           (implicit javaWriter: JavaWriter) extends NameTraverser {

  import javaWriter._

  override def traverse(name: Name): Unit = name match {
    case anonName: Name.Anonymous => nameAnonymousTraverser.traverse(anonName)
    case indeterminateName: Name.Indeterminate => nameIndeterminateTraverser.traverse(indeterminateName)
    case termName: Term.Name => termNameRenderer.render(termName)
    case typeName: Type.Name => typeNameTraverser.traverse(typeName)
    case other => writeComment(s"UNSUPPORTED: $other")
  }

}
