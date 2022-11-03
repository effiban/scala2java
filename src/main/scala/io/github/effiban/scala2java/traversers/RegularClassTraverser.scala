package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts._
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.transformers.ParamToDeclValTransformer
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait RegularClassTraverser {
  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Unit
}

private[traversers] class RegularClassTraverserImpl(modListTraverser: => ModListTraverser,
                                                    typeParamListTraverser: => TypeParamListTraverser,
                                                    templateTraverser: => TemplateTraverser,
                                                    paramToDeclValTransformer: ParamToDeclValTransformer,
                                                    javaTreeTypeResolver: JavaTreeTypeResolver,
                                                    javaChildScopeResolver: JavaChildScopeResolver)
                                                   (implicit javaWriter: JavaWriter) extends RegularClassTraverser {

  import javaWriter._

  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Unit = {
    writeLine()
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(classDef, classDef.mods))
    modListTraverser.traverse(toJavaModifiersContext(classDef, javaTreeType, context.javaScope))
    writeNamedType(JavaTreeTypeToKeywordMapping(javaTreeType), classDef.name.value)
    typeParamListTraverser.traverse(classDef.tparams)
    traverseCtorAndTemplate(classDef, javaTreeType, context)
  }

  private def traverseCtorAndTemplate(classDef: Defn.Class, javaTreeType: JavaTreeType, context: ClassOrTraitContext): Unit = {
    val explicitMemberDecls = classDef.ctor.paramss.flatten.map(x =>
      paramToDeclValTransformer.transform(x)
    )
    // TODO if the ctor. params have 'ValParam' or 'VarParam' modifiers, need to generate accessors/mutators for them as well
    val enrichedStats = explicitMemberDecls ++ classDef.templ.stats
    val enrichedTemplate = classDef.templ.copy(stats = enrichedStats)
    val javaChildScope = javaChildScopeResolver.resolve(JavaChildScopeContext(classDef, javaTreeType))
    val templateContext = TemplateContext(
      javaScope = javaChildScope,
      maybeClassName = Some(classDef.name),
      maybePrimaryCtor = Some(classDef.ctor),
      permittedSubTypeNames = context.permittedSubTypeNames
    )
    templateTraverser.traverse(template = enrichedTemplate, context = templateContext)
  }

  private def toJavaModifiersContext(classDef: Defn.Class,
                                     javaTreeType: JavaTreeType,
                                     javaScope: JavaScope) =
    JavaModifiersContext(
      scalaTree = classDef,
      scalaMods = classDef.mods,
      javaTreeType = javaTreeType,
      javaScope = javaScope
    )
}
