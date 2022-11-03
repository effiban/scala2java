package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts._
import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.entities.JavaTreeTypeToKeywordMapping
import io.github.effiban.scala2java.resolvers.{JavaChildScopeResolver, JavaTreeTypeResolver}
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Defn

trait CaseClassTraverser {
  def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Unit
}

private[traversers] class CaseClassTraverserImpl(modListTraverser: => ModListTraverser,
                                                 typeParamListTraverser: => TypeParamListTraverser,
                                                 termParamListTraverser: => TermParamListTraverser,
                                                 templateTraverser: => TemplateTraverser,
                                                 javaTreeTypeResolver: JavaTreeTypeResolver,
                                                 javaChildScopeResolver: JavaChildScopeResolver)
                                                (implicit javaWriter: JavaWriter) extends CaseClassTraverser {

  import javaWriter._

  override def traverse(classDef: Defn.Class, context: ClassOrTraitContext = ClassOrTraitContext()): Unit = {
    writeLine()
    val javaTreeType = javaTreeTypeResolver.resolve(JavaTreeTypeContext(classDef, classDef.mods))
    modListTraverser.traverse(toJavaModifiersContext(classDef, javaTreeType, context.javaScope))
    writeNamedType(JavaTreeTypeToKeywordMapping(javaTreeType), classDef.name.value)
    typeParamListTraverser.traverse(classDef.tparams)
    traverseCtorAndTemplate(classDef, javaTreeType, context)
  }

  private def traverseCtorAndTemplate(classDef: Defn.Class, javaTreeType: JavaTreeType, context: ClassOrTraitContext): Unit = {
    val javaChildScope = javaChildScopeResolver.resolve(JavaChildScopeContext(classDef, javaTreeType))
    termParamListTraverser.traverse(classDef.ctor.paramss.flatten, StatContext(javaChildScope))
    // Even though the Java type is a Record, the constructor must still be explicitly declared if it has modifiers (annotations, visibility, etc.)
    val maybePrimaryCtor = if (classDef.ctor.mods.nonEmpty) Some(classDef.ctor) else None
    val templateContext = TemplateContext(
      javaScope = javaChildScope,
      maybeClassName = Some(classDef.name),
      maybePrimaryCtor = maybePrimaryCtor,
      permittedSubTypeNames = context.permittedSubTypeNames
    )
    templateTraverser.traverse(template = classDef.templ, context = templateContext)
  }

  private def toJavaModifiersContext(classDef: Defn.Class,
                                     javaTreeType: JavaTreeType,
                                     javaScope: JavaScope) =
    JavaModifiersContext(
      scalaTree = classDef,
      scalaMods = classDef.mods,
      javaTreeType = javaTreeType,
      javaScope = javaScope
    )
}
