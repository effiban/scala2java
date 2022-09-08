package effiban.scala2java.traversers

import effiban.scala2java.classifiers.JavaStatClassifier
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Ctor, Init, Stat, Tree, Type}

trait TemplateChildTraverser {
  def traverse(child: Tree,
               inits: List[Init],
               maybeClassName: Option[Type.Name] = None): Unit
}

private[traversers] class TemplateChildTraverserImpl(ctorPrimaryTraverser: => CtorPrimaryTraverser,
                                                     ctorSecondaryTraverser: => CtorSecondaryTraverser,
                                                     statTraverser: => StatTraverser,
                                                     javaStatClassifier: JavaStatClassifier)
                                                    (implicit javaWriter: JavaWriter) extends TemplateChildTraverser {

  import javaWriter._

  override def traverse(child: Tree,
                        inits: List[Init],
                        maybeClassName: Option[Type.Name] = None): Unit = child match {
    case primaryCtor: Ctor.Primary => traversePrimaryCtor(primaryCtor, maybeClassName, inits)
    case secondaryCtor: Ctor.Secondary => traverseSecondaryCtor(secondaryCtor, maybeClassName)
    case stat: Stat => traverseNonConstructorStat(stat)
    case unexpected: Tree => throw new IllegalStateException(s"Unexpected template child: $unexpected")
  }

  private def traversePrimaryCtor(primaryCtor: Ctor.Primary,
                                  maybeClassName: Option[Type.Name],
                                  inits: List[Init]): Unit = {
    maybeClassName match {
      // TODO skip traversal if the ctor. is public+default+empty, and there are no secondaries
      case Some(className) => ctorPrimaryTraverser.traverse(primaryCtor, className, inits)
      case None => throw new IllegalStateException("Primary Ctor. exists but class name was not passed to the TemplateTraverser")
    }
  }

  private def traverseSecondaryCtor(secondaryCtor: Ctor.Secondary, maybeClassName: Option[Type.Name]): Unit = {
    maybeClassName match {
      case Some(className) => ctorSecondaryTraverser.traverse(secondaryCtor, className)
      case None => throw new IllegalStateException("Secondary Ctor. exists but class name was not passed to the TemplateTraverser")
    }
  }

  private def traverseNonConstructorStat(stat: Stat): Unit = {
    statTraverser.traverse(stat)
    if (javaStatClassifier.requiresEndDelimiter(stat)) {
      writeStatementEnd()
    }
  }
}
