package io.github.effiban.scala2java.core.traversers

import scala.meta.Init

trait TemplateInitTraverser extends ScalaTreeTraverser1[Init]

private[traversers] class TemplateInitTraverserImpl(typeTraverser: => TypeTraverser) extends TemplateInitTraverser {

  def traverse(init: Init): Init = {
    val clearedInit = clearInitArgs(init)
    clearedInit.copy(tpe = typeTraverser.traverse(init.tpe))
  }

  private def clearInitArgs(init: Init) = {
    // Clearing the arguments because before this traverser is called,
    // they are moved into the super call of the Java-style primary ctor - and we don't want to manipulate them any further.
    // *NOTE* that there's a difference between no args at all - which indicates a trait,
    // and args with a nested empty list - which indicates a class.
    // We need to keep this distinction in order to resolve which inheritance keyword to use later on.
    init.copy(argss = init.argss.map(_ => Nil))
  }
}
