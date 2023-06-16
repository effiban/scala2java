package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ModifiersContext
import io.github.effiban.scala2java.core.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Mod
import scala.meta.Mod.Implicit

@deprecated
trait DeprecatedModListTraverser {
  def traverse(modifiersContext: ModifiersContext, annotsOnSameLine: Boolean = false): Unit
}

@deprecated
class DeprecatedModListTraverserImpl(annotListTraverser: => DeprecatedAnnotListTraverser,
                                     javaModifiersResolver: JavaModifiersResolver)
                                    (implicit javaWriter: JavaWriter) extends DeprecatedModListTraverser {

  import javaWriter._

  override def traverse(modifiersContext: ModifiersContext, annotsOnSameLine: Boolean = false): Unit = {
    annotListTraverser.traverseMods(modifiersContext.scalaMods, annotsOnSameLine)
    handleImplicitIfExists(modifiersContext.scalaMods)
    writeModifiers(javaModifiersResolver.resolve(modifiersContext))
  }

  private def handleImplicitIfExists(scalaMods: List[Mod]): Unit = {
    if (scalaMods.exists(_.isInstanceOf[Implicit])) {
      writeComment("implicit")
    }
  }
}
