package com.effiban.scala2java

import scala.meta.Name

object NameAnonymousTraverser extends ScalaTreeTraverser[Name.Anonymous] {

  // Type with no explicit name, by default should be left empty in Java
  // (except special cases e.g. `this` and `super` which are handled in their traversers)
  def traverse(anonymousName: Name.Anonymous): Unit = {
  }
}
