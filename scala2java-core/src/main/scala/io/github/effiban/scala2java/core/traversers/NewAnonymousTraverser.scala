package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TemplateContext
import io.github.effiban.scala2java.core.entities.JavaKeyword.New
import io.github.effiban.scala2java.core.entities.JavaScope
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.NewAnonymous

trait NewAnonymousTraverser extends ScalaTreeTraverser[NewAnonymous]

private[traversers] class NewAnonymousTraverserImpl(templateTraverser: => TemplateTraverser)
                                                   (implicit javaWriter: JavaWriter) extends NewAnonymousTraverser {

  import javaWriter._

  override def traverse(newAnonymous: NewAnonymous): Unit = {
    writeKeyword(New)
    write(" ")
    templateTraverser.traverse(newAnonymous.templ, TemplateContext(javaScope = JavaScope.Class))
  }
}
