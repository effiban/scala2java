package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Term.NewAnonymous

trait NewAnonymousTraverser extends ScalaTreeTraverser[NewAnonymous]

private[scala2java] class NewAnonymousTraverserImpl(templateTraverser: => TemplateTraverser)
                                                   (implicit javaEmitter: JavaEmitter) extends NewAnonymousTraverser {

  import javaEmitter._

  override def traverse(newAnonymous: NewAnonymous): Unit = {
    emit("new ")
    templateTraverser.traverse(newAnonymous.templ)
  }
}

object NewAnonymousTraverser extends NewAnonymousTraverserImpl(TemplateTraverser)
