package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.{Init, Name}

trait InitTraverser extends ScalaTreeTraverser[Init]

object InitTraverser extends InitTraverser {

  // An 'Init' is a parent of a type in its declaration
  override def traverse(init: Init): Unit = {
    TypeTraverser.traverse(init.tpe)
    init.name match {
      case Name.Anonymous() =>
      case name =>
        emit(" ")
        NameTraverser.traverse(name)
    }
    val args = init.argss.flatten
    if (args.nonEmpty) {
      TermListTraverser.traverse(init.argss.flatten, maybeDelimiterType = Some(Parentheses))
    }
  }
}
