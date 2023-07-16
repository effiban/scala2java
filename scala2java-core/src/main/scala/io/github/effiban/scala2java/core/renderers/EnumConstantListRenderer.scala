package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Defn, Pat}

trait EnumConstantListRenderer extends JavaTreeRenderer[Defn.Var]

private[renderers] class EnumConstantListRendererImpl(argumentListRenderer: => ArgumentListRenderer)
                                                      (implicit javaWriter: JavaWriter) extends EnumConstantListRenderer {

  import javaWriter._

  def render(enumConstantsVar: Defn.Var): Unit = {
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
