package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.entities.JavaScope.Block
import io.github.effiban.scala2java.core.entities.TraversalConstants.UnknownType
import io.github.effiban.scala2java.core.typeinference.TermTypeInferrer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Term, Type}

trait DefnValOrVarTypeTraverser {
  def traverse(maybeDeclType: Option[Type],
               rhs: Option[Term],
               context: StatContext = StatContext()): Unit
}

private[traversers] class DefnValOrVarTypeTraverserImpl(typeTraverser: => TypeTraverser,
                                                        termTypeInferrer: => TermTypeInferrer)
                                                       (implicit javaWriter: JavaWriter) extends DefnValOrVarTypeTraverser {

  import javaWriter._

  override def traverse(maybeDeclType: Option[Type],
                        maybeRhs: Option[Term],
                        context: StatContext = StatContext()): Unit = {
    (maybeDeclType, maybeRhs) match {
      case (Some(declType), _) => typeTraverser.traverse(declType)
      case (None, _) if context.javaScope == Block => write("var")
      case (None, Some(rhs)) => inferTypeIfPossible(rhs)
      case _ => handleUnknownType()
    }
  }

  private def inferTypeIfPossible(rhs: Term): Unit = {
    termTypeInferrer.infer(rhs) match {
      case Some(tpe) => typeTraverser.traverse(tpe)
      case None => handleUnknownType()
    }
  }

  private def handleUnknownType(): Unit = writeComment(UnknownType)
}
