package effiban.scala2java.traversers

import effiban.scala2java.contexts.{JavaModifiersContext, JavaTreeTypeContext, StatContext}
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
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
    writeTypeDeclaration(modifiers = resolveJavaModifiers(objectDef, javaTreeType),
      typeKeyword = JavaTreeTypeToKeywordMapping(javaTreeType),
      name = s"${objectDef.name.toString}")
    val outerJavaScope = javaScope
    javaScope = JavaTreeTypeToScopeMapping(javaTreeType)
    templateTraverser.traverse(objectDef.templ)
    javaScope = outerJavaScope
  }

  private def resolveJavaModifiers(objectDef: Defn.Object, javaTreeType: JavaTreeType) = {
    val context = JavaModifiersContext(
      scalaTree = objectDef,
      scalaMods = objectDef.mods,
      javaTreeType = javaTreeType,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(context)
  }
}

