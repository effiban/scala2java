package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter
import effiban.scala2java.writers.JavaWriter.writeComment

import scala.meta.{Name, Term, Type}

trait NameTraverser extends ScalaTreeTraverser[Name]

private[traversers] class NameTraverserImpl(nameAnonymousTraverser: => NameAnonymousTraverser,
                                            nameIndeterminateTraverser: => NameIndeterminateTraverser,
                                            termNameTraverser: => TermNameTraverser,
                                            typeNameTraverser: => TypeNameTraverser)
                                           (implicit javaWriter: JavaWriter) extends NameTraverser {

  override def traverse(name: Name): Unit = name match {
    case anonName: Name.Anonymous => nameAnonymousTraverser.traverse(anonName)
    case indeterminateName: Name.Indeterminate => nameIndeterminateTraverser.traverse(indeterminateName)
    case termName: Term.Name => termNameTraverser.traverse(termName)
    case typeName: Type.Name => typeNameTraverser.traverse(typeName)
    case other => writeComment(s"UNSUPPORTED: $other")
  }

}
