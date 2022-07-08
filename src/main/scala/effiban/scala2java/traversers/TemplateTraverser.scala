package effiban.scala2java.traversers

import effiban.scala2java.orderings.JavaTemplateChildOrdering
import effiban.scala2java.{ClassInfo, JavaEmitter}

import scala.meta.{Ctor, Defn, Init, Name, Stat, Template, Term, Tree, Type}

trait TemplateTraverser extends ScalaTreeTraverser[Template] {

  def traverse(template: Template,
               maybeClassInfo: Option[ClassInfo] = None): Unit
}

private[scala2java] class TemplateTraverserImpl(initListTraverser: => InitListTraverser,
                                                selfTraverser: => SelfTraverser,
                                                statTraverser: => StatTraverser,
                                                ctorPrimaryTraverser: => CtorPrimaryTraverser,
                                                ctorSecondaryTraverser: => CtorSecondaryTraverser,
                                                javaTemplateChildOrdering: JavaTemplateChildOrdering)
                                               (implicit javaEmitter: JavaEmitter) extends TemplateTraverser {

  import javaEmitter._

  private val ParentsToSkip = Set(Term.Name("AnyRef"), Term.Name("Product"), Term.Name("Serializable"))

  override def traverse(template: Template): Unit = {
    traverse(template, None)
  }

  def traverse(template: Template,
               maybeClassInfo: Option[ClassInfo] = None): Unit = {
    val relevantInits = template.inits.filterNot(init => shouldSkipParent(init.name))
    traverseTemplateInits(relevantInits)
    selfTraverser.traverse(template.self)
    traverseTemplateBody(
      statements = template.stats,
      inits = relevantInits,
      maybeClassInfo = maybeClassInfo)
  }

  private def traverseTemplateInits(relevantInits: List[Init]): Unit = {
    if (relevantInits.nonEmpty) {
      emitInheritanceKeyword()
      initListTraverser.traverse(relevantInits)
    }
  }

  private def shouldSkipParent(parent: Name): Boolean = {
    ParentsToSkip.exists(parentToSkip => parentToSkip.structure == parent.structure)
  }

  private def traverseTemplateBody(statements: List[Stat],
                                   inits: List[Init],
                                   maybeClassInfo: Option[ClassInfo] = None): Unit = {
    val children = statements ++ maybeClassInfo.flatMap(_.maybePrimaryCtor)
    val maybeClassName = maybeClassInfo.map(_.className)
    emitBlockStart()
    children.sorted(javaTemplateChildOrdering).foreach {
      case defnDef: Defn.Def => statTraverser.traverse(defnDef)
      case defnType: Defn.Type => statTraverser.traverse(defnType)
      case primaryCtor: Ctor.Primary => traversePrimaryCtor(primaryCtor, maybeClassName, inits)
      case secondaryCtor: Ctor.Secondary => traverseSecondaryCtor(secondaryCtor, maybeClassName)
      case stat: Stat =>
        statTraverser.traverse(stat)
        emitStatementEnd()
      case unexpected: Tree => throw new IllegalStateException(s"Unexpected template child: $unexpected")
    }
    emitBlockEnd()
  }

  def traversePrimaryCtor(primaryCtor: Ctor.Primary,
                          maybeClassName: Option[Type.Name],
                          inits: List[Init]): Unit = {
    maybeClassName match {
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
}

object TemplateTraverser extends TemplateTraverserImpl(
  InitListTraverser,
  SelfTraverser,
  StatTraverser,
  CtorPrimaryTraverser,
  CtorSecondaryTraverser,
  JavaTemplateChildOrdering
)
