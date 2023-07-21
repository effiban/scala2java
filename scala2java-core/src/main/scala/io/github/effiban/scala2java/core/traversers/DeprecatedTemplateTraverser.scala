package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{TemplateBodyContext, TemplateContext}
import io.github.effiban.scala2java.core.renderers.contexts.InitRenderContext
import io.github.effiban.scala2java.core.renderers.{InitListRenderer, PermittedSubTypeNameListRenderer, SelfRenderer}
import io.github.effiban.scala2java.core.resolvers.JavaInheritanceKeywordResolver
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.predicates.TemplateInitExcludedPredicate

import scala.meta.{Init, Template}

@deprecated
trait DeprecatedTemplateTraverser {

  def traverse(template: Template, context: TemplateContext): Unit
}

@deprecated
private[traversers] class DeprecatedTemplateTraverserImpl(initTraverser: => InitTraverser,
                                                          initListRenderer: => InitListRenderer,
                                                          selfTraverser: => SelfTraverser,
                                                          selfRenderer: SelfRenderer,
                                                          templateBodyTraverser: => DeprecatedTemplateBodyTraverser,
                                                          permittedSubTypeNameListRenderer: => PermittedSubTypeNameListRenderer,
                                                          javaInheritanceKeywordResolver: JavaInheritanceKeywordResolver,
                                                          templateInitExcludedPredicate: TemplateInitExcludedPredicate)
                                                         (implicit javaWriter: JavaWriter) extends DeprecatedTemplateTraverser {

  import javaWriter._

  def traverse(template: Template, context: TemplateContext): Unit = {
    val includedInits = template.inits.filterNot(templateInitExcludedPredicate)
    traverseTemplateInits(includedInits, context.javaScope)
    val traversedSelf = selfTraverser.traverse(template.self)
    selfRenderer.render(traversedSelf)
    if (context.permittedSubTypeNames.nonEmpty) {
      write(" ")
      permittedSubTypeNameListRenderer.render(context.permittedSubTypeNames)
    }
    val bodyContext = TemplateBodyContext(
      javaScope = context.javaScope,
      maybeClassName = context.maybeClassName,
      maybePrimaryCtor = context.maybePrimaryCtor,
      inits = includedInits
    )
    templateBodyTraverser.traverse(statements = template.stats, context = bodyContext)
  }

  private def traverseTemplateInits(inits: List[Init], javaScope: JavaScope): Unit = {
    if (inits.nonEmpty) {
      val inheritanceKeyword = javaInheritanceKeywordResolver.resolve(javaScope, inits)
      write(" ")
      writeKeyword(inheritanceKeyword)
      write(" ")
      val traversedInits = inits.map(initTraverser.traverse)
      initListRenderer.render(traversedInits, InitRenderContext(ignoreArgs = true))
    }
  }
}
