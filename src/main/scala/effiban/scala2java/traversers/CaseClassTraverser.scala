package effiban.scala2java.traversers

import effiban.scala2java.entities.ClassInfo
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.{JavaEmitter, entities}

import scala.meta.Defn

trait CaseClassTraverser extends ScalaTreeTraverser[Defn.Class]

private[scala2java] class CaseClassTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                 typeParamListTraverser: => TypeParamListTraverser,
                                                 termParamListTraverser: => TermParamListTraverser,
                                                 templateTraverser: => TemplateTraverser,
                                                 javaModifiersResolver: JavaModifiersResolver)
                                                (implicit javaEmitter: JavaEmitter) extends CaseClassTraverser {

  import javaEmitter._

  def traverse(classDef: Defn.Class): Unit = {
    emitLine()
    annotListTraverser.traverseMods(classDef.mods)
    emitTypeDeclaration(modifiers = javaModifiersResolver.resolveForClass(classDef.mods),
      typeKeyword = "record",
      name = classDef.name.value)
    typeParamListTraverser.traverse(classDef.tparams)
    termParamListTraverser.traverse(classDef.ctor.paramss.flatten)
    val outerJavaScope = javaScope
    javaScope = entities.Class
    templateTraverser.traverse(template = classDef.templ, maybeClassInfo = Some(ClassInfo(className = classDef.name, maybePrimaryCtor = None)))
    javaScope = outerJavaScope
  }
}

object CaseClassTraverser extends CaseClassTraverserImpl(
  AnnotListTraverser,
  TypeParamListTraverser,
  TermParamListTraverser,
  TemplateTraverser,
  JavaModifiersResolver
)
