package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts._
import io.github.effiban.scala2java.core.renderers.contexts.{DefRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.renderers.{DeclVarRenderer, DefnDefRenderer, DefnVarRenderer}
import io.github.effiban.scala2java.core.traversers.results.{DeclVarTraversalResult, DefnVarTraversalResult}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Defn
import scala.meta.Defn.Trait

@deprecated
trait DeprecatedDefnTraverser {
  def traverse(defn: Defn, context: StatContext = StatContext()): Unit
}

@deprecated
private[traversers] class DeprecatedDefnTraverserImpl(declVarRenderer: => DeclVarRenderer,
                                                      defnVarTraverser: => DefnVarTraverser,
                                                      defnVarRenderer: => DefnVarRenderer,
                                                      defnDefTraverser: => DefnDefTraverser,
                                                      defnDefRenderer: => DefnDefRenderer,
                                                      classTraverser: => DeprecatedClassTraverser,
                                                      traitTraverser: => DeprecatedTraitTraverser,
                                                      objectTraverser: => DeprecatedObjectTraverser)
                                                     (implicit javaWriter: JavaWriter) extends DeprecatedDefnTraverser {

  import javaWriter._

  override def traverse(defn: Defn, context: StatContext = StatContext()): Unit = defn match {
    case varDef: Defn.Var => traverseDefnVar(varDef, context)
    case defDef: Defn.Def => traverseDefnDef(context, defDef)
    case classDef: Defn.Class => classTraverser.traverse(classDef, ClassOrTraitContext(context.javaScope))
    case traitDef: Trait => traitTraverser.traverse(traitDef, ClassOrTraitContext(context.javaScope))
    case objectDef: Defn.Object => objectTraverser.traverse(objectDef, context)
    case _ => writeComment(s"UNSUPPORTED: $defn")
  }

  private def traverseDefnVar(defnVar: Defn.Var, context: StatContext): Unit = {
    val traversalResult = defnVarTraverser.traverse(defnVar, context)
    val renderContext = VarRenderContext(traversalResult.javaModifiers)
    traversalResult match {
      case declVarResult: DeclVarTraversalResult => declVarRenderer.render(declVarResult.tree, renderContext)
      case defnVarResult: DefnVarTraversalResult => defnVarRenderer.render(defnVarResult.tree, renderContext)
      case _ => writeComment(s"UNSUPPORTED: ${traversalResult.tree}")
    }
  }

  private def traverseDefnDef(context: StatContext, defDef: Defn.Def): Unit = {
    val traversalResult = defnDefTraverser.traverse(defDef, DefnDefContext(context.javaScope))
    val renderContext = DefRenderContext(traversalResult.javaModifiers)
    defnDefRenderer.render(traversalResult.tree, renderContext)
  }
}
