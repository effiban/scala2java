package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.renderers._
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.entities.JavaScope.MethodSignature

import scala.meta.{Decl, Type}

trait DeclDefTraverser {
  def traverse(defDecl: Decl.Def, context: StatContext = StatContext()): Unit
}

private[traversers] class DeclDefTraverserImpl(statModListTraverser: => StatModListTraverser,
                                               modifiersRenderContextFactory: ModifiersRenderContextFactory,
                                               modListRenderer: => ModListRenderer,
                                               typeParamTraverser: => TypeParamTraverser,
                                               typeParamListRenderer: => TypeParamListRenderer,
                                               typeTraverser: => TypeTraverser,
                                               typeRenderer: => TypeRenderer,
                                               termNameRenderer: TermNameRenderer,
                                               termParamTraverser: => TermParamTraverser,
                                               termParamListRenderer: => TermParamListRenderer)
                                              (implicit javaWriter: JavaWriter) extends DeclDefTraverser {

  import javaWriter._

  override def traverse(defDecl: Decl.Def, context: StatContext = StatContext()): Unit = {
    writeLine()
    val modListTraversalResult = statModListTraverser.traverse(ModifiersContext(defDecl, JavaTreeType.Method, context.javaScope))
    val modifiersRenderContext = modifiersRenderContextFactory(modListTraversalResult)
    modListRenderer.render(modifiersRenderContext)
    traverseTypeParams(defDecl.tparams)
    val traversedType = typeTraverser.traverse(defDecl.decltpe)
    typeRenderer.render(traversedType)
    write(" ")
    termNameRenderer.render(defDecl.name)
    val traversedParams = defDecl.paramss.flatten.map(param => termParamTraverser.traverse(param, StatContext(MethodSignature)))
    termParamListRenderer.render(traversedParams)
  }

  private def traverseTypeParams(tparams: List[Type.Param]): Unit = {
    tparams match {
      case Nil =>
      case typeParams =>
        val traversedTypeParams = typeParams.map(typeParamTraverser.traverse)
        typeParamListRenderer.render(traversedTypeParams)
        write(" ")
    }
  }
}
