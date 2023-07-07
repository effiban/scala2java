package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{StatContext, ValOrVarRenderContext}
import io.github.effiban.scala2java.core.renderers.DeclVarRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Decl

trait DeclTraverser {
  def traverse(decl: Decl, context: StatContext = StatContext()): Unit
}

private[traversers] class DeclTraverserImpl(declValTraverser: => DeclValTraverser,
                                            declVarTraverser: => DeclVarTraverser,
                                            declVarRenderer: => DeclVarRenderer,
                                            declDefTraverser: => DeclDefTraverser,
                                            declTypeTraverser: => DeclTypeTraverser)
                                           (implicit javaWriter: JavaWriter) extends DeclTraverser {

  import javaWriter._

  override def traverse(decl: Decl, context: StatContext = StatContext()): Unit = decl match {
    case valDecl: Decl.Val => declValTraverser.traverse(valDecl, context)
    case varDecl: Decl.Var =>
      val traversalResult = declVarTraverser.traverse(varDecl, context)
      val renderContext = ValOrVarRenderContext(traversalResult.javaModifiers)
      declVarRenderer.render(traversalResult.tree, renderContext);
    case defDecl: Decl.Def => declDefTraverser.traverse(defDecl, context)
    case typeDecl: Decl.Type => declTypeTraverser.traverse(typeDecl, context)
    case _ => writeComment(s"UNSUPPORTED: $decl")
  }
}
