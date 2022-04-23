package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{Parentheses, emitLine, emitTypeDeclaration}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.{Defn, Mod}

object ClassTraverser extends ScalaTreeTraverser[Defn.Class] {

  def traverse(classDef: Defn.Class): Unit = {
    emitLine()
    AnnotListTraverser.traverseMods(classDef.mods)

    if (classDef.mods.exists(_.isInstanceOf[Mod.Case])) {
      traverseCaseClassDef(classDef)
    } else {
      traverseRegularClassDef(classDef)
    }
  }

  private def traverseCaseClassDef(classDef: Defn.Class): Unit = {
    emitTypeDeclaration(modifiers = JavaModifiersResolver.resolveForClass(classDef.mods),
      typeKeyword = "record",
      name = classDef.name.toString)
    // TODO - traverse type params
    ArgumentListTraverser.traverse(classDef.ctor.paramss.flatten, maybeDelimiterType = Some(Parentheses))
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Class
    GenericTreeTraverser.traverse(classDef.templ)
    javaOwnerContext = outerJavaOwnerContext
  }

  private def traverseRegularClassDef(classDef: Defn.Class): Unit = {
    emitTypeDeclaration(modifiers = JavaModifiersResolver.resolveForClass(classDef.mods),
      typeKeyword = "class",
      name = classDef.name.toString)
    // TODO - traverse type params

    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Class
    TemplateTraverser.traverseTemplate(template = classDef.templ,
      maybeExplicitPrimaryCtor = Some(classDef.ctor),
      maybeClassName = Some(classDef.name))
    javaOwnerContext = outerJavaOwnerContext
  }
}
