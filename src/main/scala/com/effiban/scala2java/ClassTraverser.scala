package com.effiban.scala2java

import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.{Defn, Mod}

trait ClassTraverser extends ScalaTreeTraverser[Defn.Class]

private[scala2java] class ClassTraverserImpl(annotListTraverser: => AnnotListTraverser,
                                             typeParamListTraverser: => TypeParamListTraverser,
                                             termParamListTraverser: => TermParamListTraverser,
                                             templateTraverser: => TemplateTraverser,
                                             javaModifiersResolver: JavaModifiersResolver)
                                            (implicit javaEmitter: JavaEmitter) extends ClassTraverser {

  import javaEmitter._

  def traverse(classDef: Defn.Class): Unit = {
    emitLine()
    annotListTraverser.traverseMods(classDef.mods)

    if (classDef.mods.exists(_.isInstanceOf[Mod.Case])) {
      traverseCaseClassDef(classDef)
    } else {
      traverseRegularClassDef(classDef)
    }
  }

  private def traverseCaseClassDef(classDef: Defn.Class): Unit = {
    emitTypeDeclaration(modifiers = javaModifiersResolver.resolveForClass(classDef.mods),
      typeKeyword = "record",
      name = classDef.name.toString)
    typeParamListTraverser.traverse(classDef.tparams)
    termParamListTraverser.traverse(classDef.ctor.paramss.flatten)
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Class
    templateTraverser.traverse(template = classDef.templ, maybeClassInfo = Some(ClassInfo(className = classDef.name)))
    javaOwnerContext = outerJavaOwnerContext
  }

  private def traverseRegularClassDef(classDef: Defn.Class): Unit = {
    emitTypeDeclaration(modifiers = javaModifiersResolver.resolveForClass(classDef.mods),
      typeKeyword = "class",
      name = classDef.name.toString)
    typeParamListTraverser.traverse(classDef.tparams)

    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Class
    templateTraverser.traverse(template = classDef.templ,
      maybeClassInfo = Some(ClassInfo(className = classDef.name, maybeExplicitPrimaryCtor = Some(classDef.ctor))))
    javaOwnerContext = outerJavaOwnerContext
  }
}

object ClassTraverser extends ClassTraverserImpl(
  AnnotListTraverser,
  TypeParamListTraverser,
  TermParamListTraverser,
  TemplateTraverser,
  JavaModifiersResolver
)
