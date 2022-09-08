package effiban.scala2java.traversers

import effiban.scala2java.entities.ClassInfo
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.orderings.JavaTemplateChildOrdering
import effiban.scala2java.resolvers.JavaInheritanceKeywordResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Init, Stat, Template, Type}

trait TemplateTraverser {

  def traverse(template: Template,
               maybeClassInfo: Option[ClassInfo] = None): Unit
}

private[traversers] class TemplateTraverserImpl(initListTraverser: => InitListTraverser,
                                                selfTraverser: => SelfTraverser,
                                                templateChildTraverser: => TemplateChildTraverser,
                                                javaTemplateChildOrdering: JavaTemplateChildOrdering,
                                                javaInheritanceKeywordResolver: JavaInheritanceKeywordResolver)
                                               (implicit javaWriter: JavaWriter) extends TemplateTraverser {

  import javaWriter._

  private val ParentsToSkip = Set(Type.Name("AnyRef"), Type.Name("Product"), Type.Name("Serializable"))

  def traverse(template: Template,
               maybeClassInfo: Option[ClassInfo] = None): Unit = {
    val relevantInits = template.inits.filterNot(init => shouldSkipParent(init.tpe))
    traverseTemplateInits(relevantInits)
    selfTraverser.traverse(template.self)
    traverseTemplateBody(
      statements = template.stats,
      inits = relevantInits,
      maybeClassInfo = maybeClassInfo)
  }

  private def traverseTemplateInits(inits: List[Init]): Unit = {
    if (inits.nonEmpty) {
      val inheritanceKeyword = javaInheritanceKeywordResolver.resolve(javaScope, inits)
      write(" ")
      writeKeyword(inheritanceKeyword)
      write(" ")
      initListTraverser.traverse(inits, ignoreArgs = true)
    }
  }

  private def shouldSkipParent(parent: Type): Boolean = {
    ParentsToSkip.exists(parentToSkip => parentToSkip.structure == parent.structure)
  }

  private def traverseTemplateBody(statements: List[Stat],
                                   inits: List[Init],
                                   maybeClassInfo: Option[ClassInfo] = None): Unit = {
    val children = statements ++ maybeClassInfo.flatMap(_.maybePrimaryCtor)
    writeBlockStart()
    children.sorted(javaTemplateChildOrdering).foreach(child =>
      templateChildTraverser.traverse(child, inits, maybeClassInfo.map(_.className))
    )
    writeBlockEnd()
  }
}
