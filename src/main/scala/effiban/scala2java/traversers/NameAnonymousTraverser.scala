package effiban.scala2java.traversers

import scala.meta.Name

trait NameAnonymousTraverser extends ScalaTreeTraverser[Name.Anonymous]

object NameAnonymousTraverser extends NameAnonymousTraverser {

  // Type with no explicit name, by default should be left empty in Java
  // (except special cases e.g. `this` and `super` which are handled in their traversers)
  override def traverse(anonymousName: Name.Anonymous): Unit = {
  }
}
