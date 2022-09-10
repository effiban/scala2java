package effiban.scala2java.resolvers

import effiban.scala2java.contexts.JavaTreeTypeContext
import effiban.scala2java.entities.JavaTreeType
import effiban.scala2java.entities.JavaTreeType.JavaTreeType

import scala.meta.{Decl, Defn, Mod, Pkg, Term}

trait JavaTreeTypeResolver {
  def resolve(context: JavaTreeTypeContext): JavaTreeType
}

class JavaTreeTypeResolverImpl extends JavaTreeTypeResolver {

  override def resolve(context: JavaTreeTypeContext): JavaTreeType = {
    import context._

    (tree, mods) match {
      case (_: Pkg, _) => JavaTreeType.Package
      case (_: Defn.Class, theMods) if theMods.collectFirst{ case mod: Mod.Case => mod }.nonEmpty => JavaTreeType.Record
      case (_: Defn.Class, _) | (_: Defn.Object, _) => JavaTreeType.Class
      case (_: Defn.Trait, _) | (_: Decl.Type, _) | (_: Defn.Type, _) => JavaTreeType.Interface
      case (_: Decl.Def, _) | (_: Defn.Def, _)  => JavaTreeType.Method
      case (_: Term.Function, _) => JavaTreeType.Lambda
      case (_: Decl.Val, _) | (_: Defn.Val, _) | (_: Decl.Var, _) | (_: Defn.Var, _)  => JavaTreeType.Variable
      case (_: Term.Param, _)  => JavaTreeType.Parameter
      case _ => JavaTreeType.Unknown
    }
  }
}

object JavaTreeTypeResolver extends JavaTreeTypeResolverImpl