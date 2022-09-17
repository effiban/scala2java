package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, JavaTreeTypeContext, StatContext, TemplateContext}
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.{JavaTreeTypeToKeywordMapping, JavaTreeTypeToScopeMapping}
import effiban.scala2java.resolvers.{JavaModifiersResolver, JavaTreeTypeResolver}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait ObjectTraverser {
  def traverse(objectDef: Defn.Object, context: StatContext = StatContext()): Unit
}

private[traversers] class ObjectTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                              templateTraverser: => TemplateTraverser,
                                              javaModifiersResolver: JavaModifiersResolver,
                                              javaTreeTypeResolver: JavaTreeTypeResolver)
                                             (implicit javaWriter: JavaWriter) extends ObjectTraverser {

  import javaWriter._

  override def traverse(objectDef: Defn.Object, context: StatContext = StatContext()): Unit = {
    writeLine()
    writeComment("originally a Scala object")
    writeLine()
    annotListTraverser.traverseMods(objectDef.mods)
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(objectDef, objectDef.mods))
    writeTypeDeclaration(modifiers = resolveJavaModifiers(objectDef, javaTreeType, context.javaScope),
      typeKeyword = JavaTreeTypeToKeywordMapping(javaTreeType),
      name = s"${objectDef.name.toString}")
    templateTraverser.traverse(objectDef.templ, TemplateContext(javaScope = JavaTreeTypeToScopeMapping(javaTreeType)))
  }

  private def resolveJavaModifiers(objectDef: Defn.Object,
                                   javaTreeType: JavaTreeType,
                                   parentJavaScope: JavaTreeType) = {
    val javaModifiersContext = JavaModifiersContext(
      scalaTree = objectDef,
      scalaMods = objectDef.mods,
      javaTreeType = javaTreeType,
      javaScope = parentJavaScope
    )
    javaModifiersResolver.resolve(javaModifiersContext)
  }
}

