package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.entities.JavaTreeType
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Term

trait TermParamTraverser {
  def traverse(termParam: Term.Param, context: StatContext): Unit
}

private[traversers] class TermParamTraverserImpl(modListTraverser: => ModListTraverser,
                                                 typeTraverser: => TypeTraverser,
                                                 nameTraverser: => NameTraverser)
                                                (implicit javaWriter: JavaWriter) extends TermParamTraverser {

  import javaWriter._

  // method/lambda parameter declaration
  // Note that a primary ctor. param in Scala is also a class member which requires additional handling,
  // but that aspect will be handled by one of the parent traversers before this one is called
  override def traverse(termParam: Term.Param, context: StatContext): Unit = {
    val modifiersContext = ModifiersContext(termParam, JavaTreeType.Parameter, context.javaScope)
    modListTraverser.traverse(modifiersContext, annotsOnSameLine = true)
    termParam.decltpe.foreach(declType => {
      typeTraverser.traverse(declType)
      write(" ")
    })
    nameTraverser.traverse(termParam.name)
    termParam.default.foreach(default => writeComment(s"= ${default.toString()}"))
  }
}
