package effiban.scala2java.traversers

import effiban.scala2java.contexts._
import effiban.scala2java.entities.JavaScope.JavaScope
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.JavaTreeTypeToKeywordMapping
import effiban.scala2java.resolvers.{JavaChildScopeResolver, JavaModifiersResolver, JavaTreeTypeResolver}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait ObjectTraverser {
  def traverse(objectDef: Defn.Object, context: StatContext = StatContext()): Unit
}

private[traversers] class ObjectTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                              templateTraverser: => TemplateTraverser,
                                              javaModifiersResolver: JavaModifiersResolver,
                                              javaTreeTypeResolver: JavaTreeTypeResolver,
                                              javaChildScopeResolver: JavaChildScopeResolver)
                                             (implicit javaWriter: JavaWriter) extends ObjectTraverser {

  import javaWriter._

  override def traverse(objectDef: Defn.Object, context: StatContext = StatContext()): Unit = {
    writeLine()
    annotListTraverser.traverseMods(objectDef.mods)
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(objectDef, objectDef.mods))
    writeTypeDeclaration(modifiers = resolveJavaModifiers(objectDef, javaTreeType, context.javaScope),
      typeKeyword = JavaTreeTypeToKeywordMapping(javaTreeType),
      name = s"${objectDef.name.toString}")
    val javaChildScope = javaChildScopeResolver.resolve(JavaChildScopeContext(objectDef, javaTreeType))
    // TODO iof child scope is utility class, add private ctor.
    templateTraverser.traverse(objectDef.templ, TemplateContext(javaScope = javaChildScope))
  }

  private def resolveJavaModifiers(objectDef: Defn.Object,
                                   javaTreeType: JavaTreeType,
                                   javaScope: JavaScope) = {
    val javaModifiersContext = JavaModifiersContext(
      scalaTree = objectDef,
      scalaMods = objectDef.mods,
      javaTreeType = javaTreeType,
      javaScope = javaScope
    )
    javaModifiersResolver.resolve(javaModifiersContext)
  }
}

