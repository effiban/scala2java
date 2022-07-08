package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter
import effiban.scala2java.entities.JavaScope.Interface
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver

import scala.meta.Defn.Trait

trait TraitTraverser extends ScalaTreeTraverser[Trait]

private[scala2java] class TraitTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                             typeParamListTraverser: => TypeParamListTraverser,
                                             templateTraverser: => TemplateTraverser,
                                             javaModifiersResolver: JavaModifiersResolver)
                                            (implicit javaEmitter: JavaEmitter) extends TraitTraverser {

  import javaEmitter._

  override def traverse(traitDef: Trait): Unit = {
    emitLine()
    annotListTraverser.traverseMods(traitDef.mods)
    emitTypeDeclaration(modifiers = javaModifiersResolver.resolveForInterface(traitDef.mods),
      typeKeyword = "interface",
      name = traitDef.name.toString)
    typeParamListTraverser.traverse(traitDef.tparams)
    val outerJavaScope = javaScope
    javaScope = Interface
    templateTraverser.traverse(traitDef.templ)
    javaScope = outerJavaScope
  }
}

object TraitTraverser extends TraitTraverserImpl(
  AnnotListTraverser,
  TypeParamListTraverser,
  TemplateTraverser,
  JavaModifiersResolver
)
