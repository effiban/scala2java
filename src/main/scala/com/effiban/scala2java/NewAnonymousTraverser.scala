package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.NewAnonymous

object NewAnonymousTraverser extends ScalaTreeTraverser[NewAnonymous] {

  def traverse(newAnonymous: NewAnonymous): Unit = {
    emit("new ")
    TemplateTraverser.traverse(newAnonymous.templ)
  }
}
