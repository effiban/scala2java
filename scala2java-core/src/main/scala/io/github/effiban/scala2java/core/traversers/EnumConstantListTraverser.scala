package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.ArgumentListRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Defn, Pat}

trait EnumConstantListTraverser extends ScalaTreeTraverser[Defn.Var]

private[traversers] class EnumConstantListTraverserImpl(argumentListRenderer: => ArgumentListRenderer)
                                                       (implicit javaWriter: JavaWriter) extends EnumConstantListTraverser {

  import javaWriter._

  def traverse(enumConstantsVar: Defn.Var): Unit = {
    val enumConstants = enumConstantsVar.pats.collect { case patVar: Pat.Var => patVar }
    val invalid = enumConstantsVar.pats.filterNot(_.isInstanceOf[Pat.Var])

    (enumConstants, invalid) match {
      case (Nil, _) => throw new IllegalStateException(s"No valid enum constants found in LHS of $enumConstantsVar")
      case (_, _ :: _) => throw new IllegalStateException(s"Invalid enum constants found in LHS of $enumConstantsVar")
      case (enumConsts, _) =>
        argumentListRenderer.render(
          args = enumConsts,
          argRenderer = (patVar: Pat.Var, _) => write(patVar.name.value)
        )
    }
  }
}
