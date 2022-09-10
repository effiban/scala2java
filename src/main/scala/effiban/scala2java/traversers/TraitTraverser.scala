package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, JavaTreeTypeContext}
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{JavaTreeTypeToKeywordMapping, JavaTreeTypeToScopeMapping}
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaTreeTypeResolver}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn.Trait

trait TraitTraverser extends ScalaTreeTraverser[Trait]

private[traversers] class TraitTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                             typeParamListTraverser: => TypeParamListTraverser,
                                             templateTraverser: => TemplateTraverser,
                                             javaModifiersResolver: JavaModifiersResolver,
                                             javaTreeTypeResolver: JavaTreeTypeResolver)
                                            (implicit javaWriter: JavaWriter) extends TraitTraverser {

  import javaWriter._

  override def traverse(traitDef: Trait): Unit = {
    writeLine()
    annotListTraverser.traverseMods(traitDef.mods)
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(traitDef, traitDef.mods))
    writeTypeDeclaration(modifiers = resolveJavaModifiers(traitDef, javaTreeType),
      typeKeyword = JavaTreeTypeToKeywordMapping(javaTreeType),
      name = traitDef.name.toString)
    typeParamListTraverser.traverse(traitDef.tparams)
    val outerJavaScope = javaScope
    javaScope = JavaTreeTypeToScopeMapping(javaTreeType)
    templateTraverser.traverse(traitDef.templ)
    javaScope = outerJavaScope
  }

  private def resolveJavaModifiers(traitDef: Trait, javaTreeType: JavaTreeType) = {
    val context = JavaModifiersContext(
      scalaTree = traitDef,
      scalaMods = traitDef.mods,
      javaTreeType = javaTreeType,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(context)
  }
}
