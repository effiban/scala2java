package com.effiban.scala2java

import com.effiban.scala2java.GenericTreeTraverser.{resolveJavaClassExplicitModifiers, traverseAnnotations, traverseTemplate}
import com.effiban.scala2java.JavaEmitter.{emitLine, emitParametersEnd, emitParametersStart, emitTypeDeclaration}
import com.effiban.scala2java.TraversalContext.javaOwnerContext

import scala.meta.Mod.Annot
import scala.meta.{Defn, Mod}

object ClassTraverser extends ScalaTreeTraverser[Defn.Class] {

  def traverse(classDef: Defn.Class): Unit = {
    emitLine()
    val annotations = classDef.mods
      .filter(_.isInstanceOf[Annot])
      .map(_.asInstanceOf[Annot])
    traverseAnnotations(annotations)

    if (classDef.mods.exists(_.isInstanceOf[Mod.Case])) {
      traverseCaseClassDef(classDef)
    } else {
      traverseRegularClassDef(classDef)
    }
  }

  private def traverseCaseClassDef(classDef: Defn.Class): Unit = {
    emitTypeDeclaration(modifiers = resolveJavaClassExplicitModifiers(classDef.mods),
      typeKeyword = "record",
      name = classDef.name.toString)
    // TODO - traverse type params
    emitParametersStart()
    GenericTreeTraverser.traverse(classDef.ctor.paramss.flatten)
    emitParametersEnd()
    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Class
    GenericTreeTraverser.traverse(classDef.templ)
    javaOwnerContext = outerJavaOwnerContext
  }

  private def traverseRegularClassDef(classDef: Defn.Class): Unit = {
    emitTypeDeclaration(modifiers = resolveJavaClassExplicitModifiers(classDef.mods),
      typeKeyword = "class",
      name = classDef.name.toString)
    // TODO - traverse type params

    val outerJavaOwnerContext = javaOwnerContext
    javaOwnerContext = Class
    traverseTemplate(template = classDef.templ,
      maybeExplicitPrimaryCtor = Some(classDef.ctor),
      maybeClassName = Some(classDef.name))
    javaOwnerContext = outerJavaOwnerContext
  }
}
