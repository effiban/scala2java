package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.NewAnonymous

trait NewAnonymousTraverser extends ScalaTreeTraverser[NewAnonymous]

object NewAnonymousTraverser extends NewAnonymousTraverser {

  override def traverse(newAnonymous: NewAnonymous): Unit = {
    emit("new ")
    TemplateTraverser.traverse(newAnonymous.templ)
  }
}
