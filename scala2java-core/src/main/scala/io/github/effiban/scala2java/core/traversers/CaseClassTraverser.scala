package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.core.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.renderers.{ModListRenderer, TermParamListRenderer, TypeParamListRenderer}
import io.github.effiban.scala2java.core.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Defn

trait CaseClassTraverser {
  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Unit
}

private[traversers] class CaseClassTraverserImpl(statModListTraverser: => StatModListTraverser,
                                                 modifiersRenderContextFactory: ModifiersRenderContextFactory,
                                                 modListRenderer: => ModListRenderer,
                                                 typeParamTraverser: => TypeParamTraverser,
                                                 typeParamListRenderer: => TypeParamListRenderer,
                                                 termParamTraverser: => TermParamTraverser,
                                                 termParamListRenderer: => TermParamListRenderer,
                                                 templateTraverser: => TemplateTraverser,
                                                 javaTreeTypeResolver: JavaTreeTypeResolver,
                                                 javaChildScopeResolver: JavaChildScopeResolver)
                                                (implicit javaWriter: JavaWriter) extends CaseClassTraverser {

  import javaWriter._

  override def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Unit = {
    writeLine()
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(classDef, classDef.mods))
    val modListTraversalResult = statModListTraverser.traverse(ModifiersContext(classDef, javaTreeType, context.javaScope))
    val modifiersRenderContext = modifiersRenderContextFactory(modListTraversalResult)
    modListRenderer.render(modifiersRenderContext)
    writeNamedType(JavaTreeTypeToKeywordMapping(javaTreeType), classDef.name.value)
    val traversedTypeParams = classDef.tparams.map(typeParamTraverser.traverse)
    typeParamListRenderer.render(traversedTypeParams)
    traverseCtorAndTemplate(classDef, javaTreeType, context)
  }

  private def traverseCtorAndTemplate(classDef: Defn.Class, javaTreeType: JavaTreeType, context: ClassOrTraitContext): Unit = {
    val javaChildScope = javaChildScopeResolver.resolve(JavaChildScopeContext(classDef, javaTreeType))
    val traversedParamResults = classDef.ctor.paramss.flatten.map(param => termParamTraverser.traverse(param, StatContext(javaChildScope)))
    // TODO We can usually ignore the returned Java modifiers, because Java record fields have no visibility modifiers (implicitly final)
    // TODO However we also need to support the rare case of a 'var' Scala modifier in a case class ctor. arg
    termParamListRenderer.render(traversedParamResults.map(_.tree))
    // Even though the Java type is a Record, the constructor must still be explicitly declared if it has modifiers (annotations, visibility, etc.)
    val maybePrimaryCtor = if (classDef.ctor.mods.nonEmpty) Some(classDef.ctor) else None
    val templateContext = TemplateContext(
      javaScope = javaChildScope,
      maybeClassName = Some(classDef.name),
      maybePrimaryCtor = maybePrimaryCtor,
      permittedSubTypeNames = context.permittedSubTypeNames
    )
    templateTraverser.traverse(template = classDef.templ, context = templateContext)
  }
}
