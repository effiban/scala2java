package com.effiban.scala2java

import scala.meta.Name

class StubNameIndeterminateTraverser(implicit javaEmitter: JavaEmitter) extends NameIndeterminateTraverser {
  import javaEmitter._

  override def traverse(indeterminateName: Name.Indeterminate): Unit = emit(indeterminateName.value)
}


