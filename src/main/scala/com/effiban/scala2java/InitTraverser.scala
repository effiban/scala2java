package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.{Init, Name}

trait InitTraverser extends ScalaTreeTraverser[Init]

private[scala2java] class InitTraverserImpl(typeTraverser: => TypeTraverser,
                                            nameTraverser: => NameTraverser,
                                            termListTraverser: => TermListTraverser) extends InitTraverser {

  // An 'Init' is a parent of a type in its declaration
  override def traverse(init: Init): Unit = {
    typeTraverser.traverse(init.tpe)
    init.name match {
      case Name.Anonymous() =>
      case name =>
        emit(" ")
        nameTraverser.traverse(name)
    }
    val args = init.argss.flatten
    if (args.nonEmpty) {
      termListTraverser.traverse(init.argss.flatten, maybeDelimiterType = Some(Parentheses))
    }
  }
}

object InitTraverser extends InitTraverserImpl(TypeTraverser, NameTraverser, TermListTraverser)
