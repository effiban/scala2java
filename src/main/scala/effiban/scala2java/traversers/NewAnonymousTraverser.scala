package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.NewAnonymous

trait NewAnonymousTraverser extends ScalaTreeTraverser[NewAnonymous]

private[scala2java] class NewAnonymousTraverserImpl(templateTraverser: => TemplateTraverser)
                                                   (implicit javaWriter: JavaWriter) extends NewAnonymousTraverser {

  import javaWriter._

  override def traverse(newAnonymous: NewAnonymous): Unit = {
    write("new ")
    templateTraverser.traverse(newAnonymous.templ)
  }
}

object NewAnonymousTraverser extends NewAnonymousTraverserImpl(TemplateTraverser)
