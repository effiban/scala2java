package effiban.scala2java.traversers

import effiban.scala2java.contexts.JavaModifiersContext
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{ClassInfo, JavaTreeType}
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
    writeTypeDeclaration(modifiers = resolveJavaModifiers(classDef),
      typeKeyword = "record",
      name = classDef.name.value)
    typeParamListTraverser.traverse(classDef.tparams)
    val outerJavaScope = javaScope
    javaScope = JavaTreeType.Class
    termParamListTraverser.traverse(classDef.ctor.paramss.flatten)
    // Even though the Java type is a Record, the constructor must still be explicitly declared if it has modifiers (annotations, visibility, etc.)
    val maybePrimaryCtor = if (classDef.ctor.mods.nonEmpty) Some(classDef.ctor) else None
    val classInfo = ClassInfo(className = classDef.name, maybePrimaryCtor = maybePrimaryCtor)
    templateTraverser.traverse(template = classDef.templ, maybeClassInfo = Some(classInfo))
    javaScope = outerJavaScope
  }

  private def resolveJavaModifiers(classDef: Defn.Class) = {
    val context = JavaModifiersContext(
      scalaTree = classDef,
      scalaMods = classDef.mods,
      javaTreeType = JavaTreeType.Class,
      javaScope = javaScope)
    javaModifiersResolver.resolve(context)
  }
}
