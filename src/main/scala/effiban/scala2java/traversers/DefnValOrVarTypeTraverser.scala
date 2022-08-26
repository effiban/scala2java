package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaTreeType.Method
import effiban.scala2java.entities.TraversalConstants.UnknownType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.typeinference.TermTypeInferrer
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Term, Type}

trait DefnValOrVarTypeTraverser {
  def traverse(maybeDeclType: Option[Type], rhs: Option[Term]): Unit
}

private[traversers] class DefnValOrVarTypeTraverserImpl(typeTraverser: => TypeTraverser,
                                                        termTypeInferrer: => TermTypeInferrer)
                                                       (implicit javaWriter: JavaWriter) extends DefnValOrVarTypeTraverser {

  import javaWriter._

  override def traverse(maybeDeclType: Option[Type], maybeRhs: Option[Term]): Unit = {
    (maybeDeclType, maybeRhs) match {
      case (Some(declType), _) => typeTraverser.traverse(declType)
      case (None, _) if javaScope == Method => write("var")
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
