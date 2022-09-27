package effiban.scala2java.traversers

import effiban.scala2java.contexts.{InitContext, TemplateBodyContext, TemplateContext}
import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.resolvers.JavaInheritanceKeywordResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.{Init, Template, Type}

trait TemplateTraverser {

  def traverse(template: Template, context: TemplateContext): Unit
}

private[traversers] class TemplateTraverserImpl(initListTraverser: => InitListTraverser,
                                                selfTraverser: => SelfTraverser,
                                                templateBodyTraverser: => TemplateBodyTraverser,
                                                permittedSubTypeNameListTraverser: PermittedSubTypeNameListTraverser,
                                                javaInheritanceKeywordResolver: JavaInheritanceKeywordResolver)
                                               (implicit javaWriter: JavaWriter) extends TemplateTraverser {

  import javaWriter._

  private val ParentsToSkip = Set(
    Type.Name("Product"),
    Type.Name("Serializable"),
    Type.Name("Enumeration")
  )

  def traverse(template: Template, context: TemplateContext): Unit = {
    val relevantInits = template.inits.filterNot(init => shouldSkipParent(init.tpe))
    traverseTemplateInits(relevantInits, context.javaScope)
    selfTraverser.traverse(template.self)
    if (context.permittedSubTypeNames.nonEmpty) {
      write(" ")
      permittedSubTypeNameListTraverser.traverse(context.permittedSubTypeNames)
    }
    val bodyContext = TemplateBodyContext(
      javaScope = context.javaScope,
      maybeClassName = context.maybeClassName,
      maybePrimaryCtor = context.maybePrimaryCtor,
      inits = relevantInits
    )
    templateBodyTraverser.traverse(statements = template.stats, context = bodyContext)
  }

  private def traverseTemplateInits(inits: List[Init], javaScope: JavaScope): Unit = {
    if (inits.nonEmpty) {
      val inheritanceKeyword = javaInheritanceKeywordResolver.resolve(javaScope, inits)
      write(" ")
      writeKeyword(inheritanceKeyword)
      write(" ")
      initListTraverser.traverse(inits, InitContext(ignoreArgs = true))
    }
  }

  private def shouldSkipParent(parent: Type): Boolean = {
    ParentsToSkip.exists(parentToSkip => parentToSkip.structure == parent.structure)
  }
}
