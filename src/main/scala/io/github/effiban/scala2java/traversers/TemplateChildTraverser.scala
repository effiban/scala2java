package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.classifiers.{DefnValClassifier, JavaStatClassifier}
import io.github.effiban.scala2java.contexts.{CtorContext, StatContext, TemplateChildContext}
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.{Ctor, Defn, Stat, Tree, Type}

trait TemplateChildTraverser {
  def traverse(child: Tree, context: TemplateChildContext): Unit
}

private[traversers] class TemplateChildTraverserImpl(ctorPrimaryTraverser: => CtorPrimaryTraverser,
                                                     ctorSecondaryTraverser: => CtorSecondaryTraverser,
                                                     enumConstantListTraverser: => EnumConstantListTraverser,
                                                     statTraverser: => StatTraverser,
                                                     defnValClassifier: DefnValClassifier,
                                                     javaStatClassifier: JavaStatClassifier)
                                                    (implicit javaWriter: JavaWriter) extends TemplateChildTraverser {

  import javaWriter._

  override def traverse(child: Tree, context: TemplateChildContext): Unit = child match {
    case primaryCtor: Ctor.Primary => traversePrimaryCtor(primaryCtor, context)
    case secondaryCtor: Ctor.Secondary => traverseSecondaryCtor(secondaryCtor, context)
    case defnVal: Defn.Val if defnValClassifier.isEnumConstantList(defnVal, context.javaScope) =>
      enumConstantListTraverser.traverse(defnVal)
      writeStatementEnd()
    case stat: Stat => traverseNonConstructorStat(stat, context)
    case unexpected: Tree => throw new IllegalStateException(s"Unexpected template child: $unexpected")
  }

  private def traversePrimaryCtor(primaryCtor: Ctor.Primary, context: TemplateChildContext): Unit = {
    context.maybeClassName match {
      // TODO skip traversal if the ctor. is public+default+empty, and there are no secondaries
      case Some(className) => ctorPrimaryTraverser.traverse(primaryCtor, toCtorContext(context, className))
      case None => throw new IllegalStateException("Primary Ctor. exists but no context could be constructed for it")
    }
  }

  private def traverseSecondaryCtor(secondaryCtor: Ctor.Secondary, context: TemplateChildContext): Unit = {
    context.maybeClassName match {
      case Some(className) => ctorSecondaryTraverser.traverse(secondaryCtor, toCtorContext(context, className))
      case None => throw new IllegalStateException("Secondary Ctor. exists but no context could be constructed for it")
    }
  }

  private def traverseNonConstructorStat(stat: Stat, context: TemplateChildContext): Unit = {
    statTraverser.traverse(stat, StatContext(context.javaScope))
    if (javaStatClassifier.requiresEndDelimiter(stat)) {
      writeStatementEnd()
    }
  }

  private def toCtorContext(childContext: TemplateChildContext, className: Type.Name) = {
    CtorContext(
      javaScope = childContext.javaScope,
      className = className,
      inits = childContext.inits,
      terms = childContext.ctorTerms
    )
  }
}
