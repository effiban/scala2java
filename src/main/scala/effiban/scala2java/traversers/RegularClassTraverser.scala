package effiban.scala2java.traversers

import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{ClassInfo, JavaTreeType}
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaModifiersResolverParams}
import effiban.scala2java.transformers.ParamToDeclValTransformer
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait RegularClassTraverser extends ScalaTreeTraverser[Defn.Class]

private[traversers] class RegularClassTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                    typeParamListTraverser: => TypeParamListTraverser,
                                                    templateTraverser: => TemplateTraverser,
                                                    paramToDeclValTransformer: ParamToDeclValTransformer,
                                                    javaModifiersResolver: JavaModifiersResolver)
                                                   (implicit javaWriter: JavaWriter) extends RegularClassTraverser {

  import javaWriter._

  def traverse(classDef: Defn.Class): Unit = {
    writeLine()
    annotListTraverser.traverseMods(classDef.mods)
    writeTypeDeclaration(modifiers = resolveJavaModifiers(classDef),
      typeKeyword = "class",
      name = classDef.name.value)
    typeParamListTraverser.traverse(classDef.tparams)
    val outerJavaScope = javaScope
    javaScope = JavaTreeType.Class
    val explicitMemberDecls = classDef.ctor.paramss.flatten.map(x =>
      paramToDeclValTransformer.transform(x)
    )
    val enrichedStats = explicitMemberDecls ++ classDef.templ.stats
    val enrichedTemplate = classDef.templ.copy(stats = enrichedStats)
    templateTraverser.traverse(template = enrichedTemplate,
      maybeClassInfo = Some(ClassInfo(className = classDef.name, maybePrimaryCtor = Some(classDef.ctor))))
    javaScope = outerJavaScope
  }

  private def resolveJavaModifiers(classDef: Defn.Class) = {
    val params = JavaModifiersResolverParams(
      scalaTree = classDef,
      scalaMods = classDef.mods,
      javaTreeType = JavaTreeType.Class,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(params)
  }
}
