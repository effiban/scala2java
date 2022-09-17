package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, JavaTreeTypeContext, StatContext, TemplateContext}
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{JavaTreeTypeToKeywordMapping, JavaTreeTypeToScopeMapping}
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaTreeTypeResolver}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn.Trait

trait TraitTraverser {
  def traverse(traitDef: Trait, context: StatContext = StatContext()): Unit
}

private[traversers] class TraitTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                             typeParamListTraverser: => TypeParamListTraverser,
                                             templateTraverser: => TemplateTraverser,
                                             javaModifiersResolver: JavaModifiersResolver,
                                             javaTreeTypeResolver: JavaTreeTypeResolver)
                                            (implicit javaWriter: JavaWriter) extends TraitTraverser {

  import javaWriter._

  override def traverse(traitDef: Trait, context: StatContext = StatContext()): Unit = {
    writeLine()
    annotListTraverser.traverseMods(traitDef.mods)
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(traitDef, traitDef.mods))
    writeTypeDeclaration(modifiers = resolveJavaModifiers(traitDef, javaTreeType),
      typeKeyword = JavaTreeTypeToKeywordMapping(javaTreeType),
      name = traitDef.name.toString)
    typeParamListTraverser.traverse(traitDef.tparams)
    val outerJavaScope = javaScope
    javaScope = JavaTreeTypeToScopeMapping(javaTreeType)
    templateTraverser.traverse(traitDef.templ, TemplateContext(javaScope = JavaTreeTypeToScopeMapping(javaTreeType)))
    javaScope = outerJavaScope
  }

  private def resolveJavaModifiers(traitDef: Trait, javaTreeType: JavaTreeType) = {
    val javaModifiersContext = JavaModifiersContext(
      scalaTree = traitDef,
      scalaMods = traitDef.mods,
      javaTreeType = javaTreeType,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(javaModifiersContext)
  }
}
