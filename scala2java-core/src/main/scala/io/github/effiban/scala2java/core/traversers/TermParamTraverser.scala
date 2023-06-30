package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.traversers.results.TermParamTraversalResult

import scala.meta.Term

trait TermParamTraverser {
  def traverse(termParam: Term.Param, context: StatContext): TermParamTraversalResult
}

private[traversers] class TermParamTraverserImpl(modListTraverser: => ModListTraverser,
                                                 nameTraverser: NameTraverser,
                                                 typeTraverser: => TypeTraverser,
                                                 expressionTermTraverser: => ExpressionTermTraverser) extends TermParamTraverser {

  // method/lambda parameter declaration
  // Note that a primary ctor. param in Scala is also a class member which requires additional handling,
  // but that aspect will be handled by one of the parent traversers before this one is called
  override def traverse(termParam: Term.Param, context: StatContext): TermParamTraversalResult = {
    val modifiersContext = ModifiersContext(termParam, JavaTreeType.Parameter, context.javaScope)
    val modListTraversalResult = modListTraverser.traverse(modifiersContext)
    val traversedName = nameTraverser.traverse(termParam.name)
    val maybeTraversedType = termParam.decltpe.map(typeTraverser.traverse)
    val maybeTraversedDefault = termParam.default.map(expressionTermTraverser.traverse)

    val traversedTermParam = Term.Param(
      mods = modListTraversalResult.scalaMods,
      name = traversedName,
      decltpe = maybeTraversedType,
      default = maybeTraversedDefault
    )
    TermParamTraversalResult(traversedTermParam, modListTraversalResult.javaModifiers)
  }
}
