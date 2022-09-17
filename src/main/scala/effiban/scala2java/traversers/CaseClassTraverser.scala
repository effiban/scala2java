package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, JavaTreeTypeContext, StatContext, TemplateContext}
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{JavaTreeTypeToKeywordMapping, JavaTreeTypeToScopeMapping}
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaTreeTypeResolver}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait CaseClassTraverser {
  def traverse(classDef: Defn.Class, context: StatContext = StatContext()): Unit
}

private[traversers] class CaseClassTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                                 typeParamListTraverser: => TypeParamListTraverser,
                                                 termParamListTraverser: => TermParamListTraverser,
                                                 templateTraverser: => TemplateTraverser,
                                                 javaModifiersResolver: JavaModifiersResolver,
                                                 javaTreeTypeResolver: JavaTreeTypeResolver)
                                                (implicit javaWriter: JavaWriter) extends CaseClassTraverser {

  import javaWriter._

  override def traverse(classDef: Defn.Class, context: StatContext = StatContext()): Unit = {
    writeLine()
    annotListTraverser.traverseMods(classDef.mods)
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(classDef, classDef.mods))
    writeTypeDeclaration(modifiers = resolveJavaModifiers(classDef, javaTreeType),
      typeKeyword = JavaTreeTypeToKeywordMapping(javaTreeType),
      name = classDef.name.value)
    typeParamListTraverser.traverse(classDef.tparams)
    val outerJavaScope = javaScope
    javaScope = JavaTreeTypeToScopeMapping(javaTreeType)
    val innerJavaScope = JavaTreeTypeToScopeMapping(javaTreeType)
    termParamListTraverser.traverse(classDef.ctor.paramss.flatten, StatContext(innerJavaScope))
    // Even though the Java type is a Record, the constructor must still be explicitly declared if it has modifiers (annotations, visibility, etc.)
    val maybePrimaryCtor = if (classDef.ctor.mods.nonEmpty) Some(classDef.ctor) else None
    val templateContext = TemplateContext(
      javaScope = innerJavaScope,
      maybeClassName = Some(classDef.name),
      maybePrimaryCtor = maybePrimaryCtor
    )
    templateTraverser.traverse(template = classDef.templ, context = templateContext)
    javaScope = outerJavaScope
  }

  private def resolveJavaModifiers(classDef: Defn.Class, javaTreeType: JavaTreeType) = {
    val javaModifiersContext = JavaModifiersContext(
      scalaTree = classDef,
      scalaMods = classDef.mods,
      javaTreeType = javaTreeType,
      javaScope = javaScope)
    javaModifiersResolver.resolve(javaModifiersContext)
  }
}
