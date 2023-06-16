package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.TemplateContext
import io.github.effiban.scala2java.core.entities.JavaKeyword.New
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.Term.NewAnonymous

@deprecated
trait DeprecatedNewAnonymousTraverser extends ScalaTreeTraverser[NewAnonymous]

@deprecated
private[traversers] class DeprecatedNewAnonymousTraverserImpl(templateTraverser: => TemplateTraverser)
                                                             (implicit javaWriter: JavaWriter) extends DeprecatedNewAnonymousTraverser {

  import javaWriter._

  override def traverse(newAnonymous: NewAnonymous): Unit = {
    writeKeyword(New)
    write(" ")
    templateTraverser.traverse(newAnonymous.templ, TemplateContext(javaScope = JavaScope.Class))
  }
}
