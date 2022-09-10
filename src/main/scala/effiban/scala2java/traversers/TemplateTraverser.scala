package effiban.scala2java.traversers

import effiban.scala2java.contexts.TemplateContext
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaInheritanceKeywordResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Init, Template, Type}

trait TemplateTraverser {

  def traverse(template: Template,
               context: TemplateContext = TemplateContext()): Unit
}

private[traversers] class TemplateTraverserImpl(initListTraverser: => InitListTraverser,
                                                selfTraverser: => SelfTraverser,
                                                templateBodyTraverser: => TemplateBodyTraverser,
                                                javaInheritanceKeywordResolver: JavaInheritanceKeywordResolver)
                                               (implicit javaWriter: JavaWriter) extends TemplateTraverser {

  import javaWriter._

  private val ParentsToSkip = Set(Type.Name("AnyRef"), Type.Name("Product"), Type.Name("Serializable"))

  def traverse(template: Template,
               context: TemplateContext = TemplateContext()): Unit = {
    val relevantInits = template.inits.filterNot(init => shouldSkipParent(init.tpe))
    traverseTemplateInits(relevantInits)
    selfTraverser.traverse(template.self)
    templateBodyTraverser.traverse(
      statements = template.stats,
      inits = relevantInits,
      context = context)
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
}
