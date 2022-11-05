package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.ModifiersContext
import io.github.effiban.scala2java.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Mod
import scala.meta.Mod.Implicit

trait ModListTraverser {
  def traverse(modifiersContext: ModifiersContext, annotsOnSameLine: Boolean = false): Unit
}

class ModListTraverserImpl(annotListTraverser: => AnnotListTraverser,
                           javaModifiersResolver: JavaModifiersResolver)
                          (implicit javaWriter: JavaWriter) extends ModListTraverser {

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
