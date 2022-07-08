package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{ClassInfo, JavaScope}
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.transformers.ParamToDeclValTransformer

import scala.meta.Defn

trait RegularClassTraverser extends ScalaTreeTraverser[Defn.Class]

private[scala2java] class RegularClassTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                    typeParamListTraverser: => TypeParamListTraverser,
                                                    templateTraverser: => TemplateTraverser,
                                                    paramToDeclValTransformer: ParamToDeclValTransformer,
                                                    javaModifiersResolver: JavaModifiersResolver)
                                                   (implicit javaEmitter: JavaEmitter) extends RegularClassTraverser {

  import javaEmitter._

  def traverse(classDef: Defn.Class): Unit = {
    emitLine()
    annotListTraverser.traverseMods(classDef.mods)
    emitTypeDeclaration(modifiers = javaModifiersResolver.resolveForClass(classDef.mods),
      typeKeyword = "class",
      name = classDef.name.value)
    typeParamListTraverser.traverse(classDef.tparams)
    val outerJavaScope = javaScope
    javaScope = JavaScope.Class
    val explicitMemberDecls = classDef.ctor.paramss.flatten.map(x =>
      paramToDeclValTransformer.transform(x)
    )
    val enrichedStats = explicitMemberDecls ++ classDef.templ.stats
    val enrichedTemplate = classDef.templ.copy(stats = enrichedStats)
    templateTraverser.traverse(template = enrichedTemplate,
      maybeClassInfo = Some(ClassInfo(className = classDef.name, maybePrimaryCtor = Some(classDef.ctor))))
    javaScope = outerJavaScope
  }
}

object RegularClassTraverser extends RegularClassTraverserImpl(
  AnnotListTraverser,
  TypeParamListTraverser,
  TemplateTraverser,
  ParamToDeclValTransformer,
  JavaModifiersResolver
)
