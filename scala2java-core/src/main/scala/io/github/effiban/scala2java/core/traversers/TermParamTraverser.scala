package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.renderers.{NameRenderer, TypeRenderer}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait TermParamTraverser {
  def traverse(termParam: Term.Param, context: StatContext): Unit
}

private[traversers] class TermParamTraverserImpl(modListTraverser: => ModListTraverser,
                                                 typeTraverser: => TypeTraverser,
                                                 typeRenderer: => TypeRenderer,
                                                 nameTraverser: NameTraverser,
                                                 nameRenderer: NameRenderer)
                                                (implicit javaWriter: JavaWriter) extends TermParamTraverser {

  import javaWriter._

  // method/lambda parameter declaration
  // Note that a primary ctor. param in Scala is also a class member which requires additional handling,
  // but that aspect will be handled by one of the parent traversers before this one is called
  override def traverse(termParam: Term.Param, context: StatContext): Unit = {
    val modifiersContext = ModifiersContext(termParam, JavaTreeType.Parameter, context.javaScope)
    modListTraverser.traverse(modifiersContext, annotsOnSameLine = true)
    termParam.decltpe.foreach(declType => {
      val traversedType = typeTraverser.traverse(declType)
      typeRenderer.render(traversedType)
      write(" ")
    })
    val traversedName = nameTraverser.traverse(termParam.name)
    nameRenderer.render(traversedName)
    termParam.default.foreach(default => writeComment(s"= ${default.toString()}"))
  }
}
