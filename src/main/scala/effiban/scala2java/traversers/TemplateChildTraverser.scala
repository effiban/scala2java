package effiban.scala2java.traversers

import effiban.scala2java.classifiers.{DefnValClassifier, JavaStatClassifier}
import effiban.scala2java.entities.CtorContext
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Ctor, Defn, Stat, Tree}

trait TemplateChildTraverser {
  def traverse(child: Tree,
               maybeCtorContext: Option[CtorContext] = None): Unit
}

private[traversers] class TemplateChildTraverserImpl(ctorPrimaryTraverser: => CtorPrimaryTraverser,
                                                     ctorSecondaryTraverser: => CtorSecondaryTraverser,
                                                     enumConstantListTraverser: => EnumConstantListTraverser,
                                                     statTraverser: => StatTraverser,
                                                     defnValClassifier: DefnValClassifier,
                                                     javaStatClassifier: JavaStatClassifier)
                                                    (implicit javaWriter: JavaWriter) extends TemplateChildTraverser {

  import javaWriter._

  override def traverse(child: Tree,
                        maybeCtorContext: Option[CtorContext] = None): Unit = child match {
    case primaryCtor: Ctor.Primary => traversePrimaryCtor(primaryCtor, maybeCtorContext)
    case secondaryCtor: Ctor.Secondary => traverseSecondaryCtor(secondaryCtor, maybeCtorContext)
    case defnVal: Defn.Val if defnValClassifier.isEnumConstantList(defnVal, javaScope) =>
      enumConstantListTraverser.traverse(defnVal)
      writeStatementEnd()
    case stat: Stat => traverseNonConstructorStat(stat)
    case unexpected: Tree => throw new IllegalStateException(s"Unexpected template child: $unexpected")
  }

  private def traversePrimaryCtor(primaryCtor: Ctor.Primary, maybeCtorContext: Option[CtorContext]): Unit = {
    maybeCtorContext match {
      // TODO skip traversal if the ctor. is public+default+empty, and there are no secondaries
      case Some(ctorContext) => ctorPrimaryTraverser.traverse(primaryCtor, ctorContext)
      case None => throw new IllegalStateException("Primary Ctor. exists but no context could be constructed for it")
    }
  }

  private def traverseSecondaryCtor(secondaryCtor: Ctor.Secondary, maybeCtorContext: Option[CtorContext]): Unit = {
    maybeCtorContext match {
      case Some(ctorContext) => ctorSecondaryTraverser.traverse(secondaryCtor, ctorContext)
      case None => throw new IllegalStateException("Secondary Ctor. exists but no context could be constructed for it")
    }
  }

  private def traverseNonConstructorStat(stat: Stat): Unit = {
    statTraverser.traverse(stat)
    if (javaStatClassifier.requiresEndDelimiter(stat)) {
      writeStatementEnd()
    }
  }
}
