package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.JavaModifiersContext
import io.github.effiban.scala2java.resolvers.JavaModifiersResolver
import io.github.effiban.scala2java.writers.JavaWriter

trait ModListTraverser {
  def traverse(javaModifiersContext: JavaModifiersContext, annotsOnSameLine: Boolean = false): Unit
}

class ModListTraverserImpl(annotListTraverser: => AnnotListTraverser,
                           javaModifiersResolver: JavaModifiersResolver)
                          (implicit javaWriter: JavaWriter) extends ModListTraverser {

  import javaWriter._

  override def traverse(javaModifiersContext: JavaModifiersContext, annotsOnSameLine: Boolean = false): Unit = {
    annotListTraverser.traverseMods(javaModifiersContext.scalaMods, annotsOnSameLine)
    writeModifiers(javaModifiersResolver.resolve(javaModifiersContext))
  }
}
