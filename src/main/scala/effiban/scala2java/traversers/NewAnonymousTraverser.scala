package effiban.scala2java.traversers

import effiban.scala2java.contexts.TemplateContext
import effiban.scala2java.entities.{JavaKeyword, JavaScope}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.NewAnonymous

trait NewAnonymousTraverser extends ScalaTreeTraverser[NewAnonymous]

private[traversers] class NewAnonymousTraverserImpl(templateTraverser: => TemplateTraverser)
                                                   (implicit javaWriter: JavaWriter) extends NewAnonymousTraverser {

  import javaWriter._

  override def traverse(newAnonymous: NewAnonymous): Unit = {
    writeKeyword(JavaKeyword.New)
    write(" ")
    templateTraverser.traverse(newAnonymous.templ, TemplateContext(javaScope = JavaScope.Class))
  }
}
