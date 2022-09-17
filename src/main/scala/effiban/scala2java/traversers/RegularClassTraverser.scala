package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, JavaTreeTypeContext, StatContext, TemplateContext}
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{JavaTreeTypeToKeywordMapping, JavaTreeTypeToScopeMapping}
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaTreeTypeResolver}
import effiban.scala2java.transformers.ParamToDeclValTransformer
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait RegularClassTraverser {
  def traverse(classDef: Defn.Class, context: StatContext = StatContext()): Unit
}

private[traversers] class RegularClassTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                    typeParamListTraverser: => TypeParamListTraverser,
                                                    templateTraverser: => TemplateTraverser,
                                                    paramToDeclValTransformer: ParamToDeclValTransformer,
                                                    javaModifiersResolver: JavaModifiersResolver,
                                                    javaTreeTypeResolver: JavaTreeTypeResolver)(implicit javaWriter: JavaWriter) extends RegularClassTraverser {

  import javaWriter._

  def traverse(classDef: Defn.Class, context: StatContext = StatContext()): Unit = {
    writeLine()
    annotListTraverser.traverseMods(classDef.mods)
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(classDef, classDef.mods))
    writeTypeDeclaration(modifiers = resolveJavaModifiers(classDef, javaTreeType),
      typeKeyword = JavaTreeTypeToKeywordMapping(javaTreeType),
      name = classDef.name.value)
    typeParamListTraverser.traverse(classDef.tparams)
    val outerJavaScope = javaScope
    javaScope = JavaTreeTypeToScopeMapping(javaTreeType)
    val explicitMemberDecls = classDef.ctor.paramss.flatten.map(x =>
      paramToDeclValTransformer.transform(x)
    )
    // TODO if the ctor. params have 'ValParam' or 'VarParam' modifiers, need to generate accessors/mutators for them as well
    val enrichedStats = explicitMemberDecls ++ classDef.templ.stats
    val enrichedTemplate = classDef.templ.copy(stats = enrichedStats)
    val templateContext = TemplateContext(
      javaScope = JavaTreeTypeToScopeMapping(javaTreeType),
      maybeClassName = Some(classDef.name),
      maybePrimaryCtor = Some(classDef.ctor)
    )
    templateTraverser.traverse(template = enrichedTemplate, context = templateContext)
    javaScope = outerJavaScope
  }

  private def resolveJavaModifiers(classDef: Defn.Class, javaTreeType: JavaTreeType) = {
    val javaModifiersContext = JavaModifiersContext(
      scalaTree = classDef,
      scalaMods = classDef.mods,
      javaTreeType = javaTreeType,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(javaModifiersContext)
  }
}
