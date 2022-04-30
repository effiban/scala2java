package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.NewAnonymous

trait NewAnonymousTraverser extends ScalaTreeTraverser[NewAnonymous]

private[scala2java] class NewAnonymousTraverserImpl(templateTraverser: => TemplateTraverser) extends NewAnonymousTraverser {

  override def traverse(newAnonymous: NewAnonymous): Unit = {
    emit("new ")
    templateTraverser.traverse(newAnonymous.templ)
  }
}

object NewAnonymousTraverser extends NewAnonymousTraverserImpl(TemplateTraverser)
