package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArrayInitializerSizeContext
import io.github.effiban.scala2java.core.entities.TypeSelects
import io.github.effiban.scala2java.core.resolvers.ArrayInitializerContextResolver

import scala.meta.Term.New
import scala.meta.{Init, Name, Type, XtensionQuasiquoteType}

trait NewTraverser extends ScalaTreeTraverser1[New]

private[traversers] class NewTraverserImpl(initTraverser: => InitTraverser,
                                           arrayInitializerTraverser: => ArrayInitializerTraverser,
                                           arrayInitializerContextResolver: => ArrayInitializerContextResolver) extends NewTraverser {

  override def traverse(`new`: New): New = {
    arrayInitializerContextResolver.tryResolve(`new`.init) match {
      case Some(context) => traverseAsArrayInitializer(context)
      case None => traverseRegular(`new`)
    }
  }

  private def traverseAsArrayInitializer(context: ArrayInitializerSizeContext) = {
    val traversedContext = arrayInitializerTraverser.traverseWithSize(context)
    val traversedInit = Init(
      tpe = Type.Apply(TypeSelects.ScalaArray, List(traversedContext.tpe)),
      name = Name.Anonymous(),
      argss = List(List(traversedContext.size))
    )
    New(init = traversedInit)
  }

  private def traverseRegular(`new`: New): New = {
    New(init = initTraverser.traverse(`new`.init))
  }
}
