package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.Interface
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaModifiersResolverParams}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn.Trait

trait TraitTraverser extends ScalaTreeTraverser[Trait]

private[traversers] class TraitTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                             typeParamListTraverser: => TypeParamListTraverser,
                                             templateTraverser: => TemplateTraverser,
                                             javaModifiersResolver: JavaModifiersResolver)
                                            (implicit javaWriter: JavaWriter) extends TraitTraverser {

  import javaWriter._

  override def traverse(traitDef: Trait): Unit = {
    writeLine()
    annotListTraverser.traverseMods(traitDef.mods)
    writeTypeDeclaration(modifiers = resolveJavaModifiers(traitDef),
      typeKeyword = "interface",
      name = traitDef.name.toString)
    typeParamListTraverser.traverse(traitDef.tparams)
    val outerJavaScope = javaScope
    javaScope = Interface
    templateTraverser.traverse(traitDef.templ)
    javaScope = outerJavaScope
  }

  private def resolveJavaModifiers(traitDef: Trait) = {
    val params = JavaModifiersResolverParams(
      scalaTree = traitDef,
      scalaMods = traitDef.mods,
      javaTreeType = JavaTreeType.Interface,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(params)
  }
}
