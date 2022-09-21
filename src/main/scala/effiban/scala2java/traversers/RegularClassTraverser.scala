package effiban.scala2java.traversers

import effiban.scala2java.contexts._
import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.JavaTreeTypeToKeywordMapping
import effiban.scala2java.resolvers.{JavaChildScopeResolver, JavaModifiersResolver, JavaTreeTypeResolver}
import effiban.scala2java.transformers.ParamToDeclValTransformer
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait RegularClassTraverser {
  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Unit
}

private[traversers] class RegularClassTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                    typeParamListTraverser: => TypeParamListTraverser,
                                                    templateTraverser: => TemplateTraverser,
                                                    paramToDeclValTransformer: ParamToDeclValTransformer,
                                                    javaModifiersResolver: JavaModifiersResolver,
                                                    javaTreeTypeResolver: JavaTreeTypeResolver,
                                                    javaChildScopeResolver: JavaChildScopeResolver)
                                                   (implicit javaWriter: JavaWriter) extends RegularClassTraverser {

  import javaWriter._

  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Unit = {
    writeLine()
    annotListTraverser.traverseMods(classDef.mods)
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(classDef, classDef.mods))
    writeTypeDeclaration(modifiers = resolveJavaModifiers(classDef, javaTreeType, context.javaScope),
      typeKeyword = JavaTreeTypeToKeywordMapping(javaTreeType),
      name = classDef.name.value)
    typeParamListTraverser.traverse(classDef.tparams)
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
      maybePrimaryCtor = Some(classDef.ctor)
    )
    templateTraverser.traverse(template = enrichedTemplate, context = templateContext)
  }

  private def resolveJavaModifiers(classDef: Defn.Class,
                                   javaTreeType: JavaTreeType,
                                   javaScope: JavaScope) = {
    val javaModifiersContext = JavaModifiersContext(
      scalaTree = classDef,
      scalaMods = classDef.mods,
      javaTreeType = javaTreeType,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(javaModifiersContext)
  }
}
