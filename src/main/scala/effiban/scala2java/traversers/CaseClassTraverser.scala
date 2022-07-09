package effiban.scala2java.traversers

import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{ClassInfo, JavaScope}
import effiban.scala2java.resolvers.JavaModifiersResolver
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait CaseClassTraverser extends ScalaTreeTraverser[Defn.Class]

private[traversers] class CaseClassTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                 typeParamListTraverser: => TypeParamListTraverser,
                                                 termParamListTraverser: => TermParamListTraverser,
                                                 templateTraverser: => TemplateTraverser,
                                                 javaModifiersResolver: JavaModifiersResolver)
                                                (implicit javaWriter: JavaWriter) extends CaseClassTraverser {

  import javaWriter._

  def traverse(classDef: Defn.Class): Unit = {
    writeLine()
    annotListTraverser.traverseMods(classDef.mods)
    writeTypeDeclaration(modifiers = javaModifiersResolver.resolveForClass(classDef.mods),
      typeKeyword = "record",
      name = classDef.name.value)
    typeParamListTraverser.traverse(classDef.tparams)
    termParamListTraverser.traverse(classDef.ctor.paramss.flatten)
    val outerJavaScope = javaScope
    javaScope = JavaScope.Class
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
