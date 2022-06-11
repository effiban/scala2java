package com.effiban.scala2java

import com.effiban.scala2java.TraversalContext.javaOwnerContext

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
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Class
    templateTraverser.traverse(template = classDef.templ, maybeClassInfo = Some(ClassInfo(className = classDef.name, maybeExplicitPrimaryCtor = None)))
    javaOwnerContext = outerJavaOwnerContext
  }
}

object CaseClassTraverser extends CaseClassTraverserImpl(
  AnnotListTraverser,
  TypeParamListTraverser,
  TermParamListTraverser,
  TemplateTraverser,
  JavaModifiersResolver
)
