package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext

import scala.meta.Term

trait TermParamTraverser {
  def traverse(termParam: Term.Param, context: StatContext): Term.Param
}

private[traversers] class TermParamTraverserImpl(termParamModListTraverser: => TermParamModListTraverser,
                                                 nameTraverser: NameTraverser,
                                                 typeTraverser: => TypeTraverser,
                                                 expressionTermTraverser: => ExpressionTermTraverser) extends TermParamTraverser {

  // method/lambda parameter declaration
  // Note that a primary ctor. param in Scala is also a class member which requires additional handling,
  // but that aspect will be handled by one of the parent traversers before this one is called
  override def traverse(termParam: Term.Param, context: StatContext): Term.Param = {
    val traversedMods = termParamModListTraverser.traverse(termParam, context.javaScope)
    val traversedName = nameTraverser.traverse(termParam.name)
    val maybeTraversedType = termParam.decltpe.map(typeTraverser.traverse)
    val maybeTraversedDefault = termParam.default.map(expressionTermTraverser.traverse)

    Term.Param(
      mods = traversedMods,
      name = traversedName,
      decltpe = maybeTraversedType,
      default = maybeTraversedDefault
    )
  }
}
