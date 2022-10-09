package io.github.effiban.scala2java.resolvers

import io.github.effiban.scala2java.classifiers.TemplateClassifier
import io.github.effiban.scala2java.contexts.JavaTreeTypeContext
import io.github.effiban.scala2java.entities.JavaTreeType
import io.github.effiban.scala2java.entities.JavaTreeType.JavaTreeType

import scala.meta.{Decl, Defn, Mod, Pkg, Template, Term}

trait JavaTreeTypeResolver {
  def resolve(context: JavaTreeTypeContext): JavaTreeType
}

class JavaTreeTypeResolverImpl(templateClassifier: TemplateClassifier) extends JavaTreeTypeResolver {

  override def resolve(context: JavaTreeTypeContext): JavaTreeType = {
    import context._

    (tree, mods) match {
      case (_: Pkg, _) => JavaTreeType.Package
      case (_: Defn.Class, theMods) if theMods.collectFirst { case mod: Mod.Case => mod }.nonEmpty => JavaTreeType.Record
      case (classDef: Defn.Class, _) if isEnum(classDef.templ) => JavaTreeType.Enum
      case (objectDef: Defn.Object, _) if isEnum(objectDef.templ) => JavaTreeType.Enum
      case (_: Defn.Class, _) | (_: Defn.Object, _) => JavaTreeType.Class
      case (_: Defn.Trait, _) | (_: Decl.Type, _) | (_: Defn.Type, _) => JavaTreeType.Interface
      case (_: Decl.Def, _) | (_: Defn.Def, _) => JavaTreeType.Method
      case (_: Term.Function, _) => JavaTreeType.Lambda
      case (_: Decl.Val, _) | (_: Defn.Val, _) | (_: Decl.Var, _) | (_: Defn.Var, _) => JavaTreeType.Variable
      case (_: Term.Param, _) => JavaTreeType.Parameter
      case _ => JavaTreeType.Unknown
    }
  }

  private def isEnum(template: Template) = templateClassifier.isEnum(template)

}

object JavaTreeTypeResolver extends JavaTreeTypeResolverImpl(TemplateClassifier)