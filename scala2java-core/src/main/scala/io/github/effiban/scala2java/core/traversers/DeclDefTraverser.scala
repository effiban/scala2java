package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ModifiersContext, StatContext}
import io.github.effiban.scala2java.core.entities.JavaTreeType
import io.github.effiban.scala2java.core.renderers.contextfactories.ModifiersRenderContextFactory
import io.github.effiban.scala2java.core.renderers.{ModListRenderer, TermNameRenderer, TypeRenderer}
import io.github.effiban.scala2java.core.writers.JavaWriter
import io.github.effiban.scala2java.spi.entities.JavaScope

import scala.meta.{Decl, Type}

trait DeclDefTraverser {
  def traverse(defDecl: Decl.Def, context: StatContext = StatContext()): Unit
}

private[traversers] class DeclDefTraverserImpl(modListTraverser: => ModListTraverser,
                                               modifiersRenderContextFactory: ModifiersRenderContextFactory,
                                               modListRenderer: => ModListRenderer,
                                               typeParamListTraverser: => TypeParamListTraverser,
                                               typeTraverser: => TypeTraverser,
                                               typeRenderer: => TypeRenderer,
                                               termNameRenderer: TermNameRenderer,
                                               termParamListTraverser: => DeprecatedTermParamListTraverser)
                                              (implicit javaWriter: JavaWriter) extends DeclDefTraverser {

  import javaWriter._

  override def traverse(defDecl: Decl.Def, context: StatContext = StatContext()): Unit = {
    writeLine()
    val modListTraversalResult = modListTraverser.traverse(ModifiersContext(defDecl, JavaTreeType.Method, context.javaScope))
    val modifiersRenderContext = modifiersRenderContextFactory(modListTraversalResult)
    modListRenderer.render(modifiersRenderContext)
    traverseTypeParams(defDecl.tparams)
    val traversedType = typeTraverser.traverse(defDecl.decltpe)
    typeRenderer.render(traversedType)
    write(" ")
    termNameRenderer.render(defDecl.name)
    termParamListTraverser.traverse(termParams = defDecl.paramss.flatten, context = StatContext(JavaScope.MethodSignature))
  }

  private def traverseTypeParams(tparams: List[Type.Param]): Unit = {
    tparams match {
      case Nil =>
      case typeParams =>
        typeParamListTraverser.traverse(typeParams)
        write(" ")
    }
  }
}
