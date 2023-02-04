package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Defn, Pat}

trait EnumConstantListTraverser extends ScalaTreeTraverser[Defn.Val]

private[traversers] class EnumConstantListTraverserImpl(argumentListTraverser: => ArgumentListTraverser)
                                                       (implicit javaWriter: JavaWriter) extends EnumConstantListTraverser {

  import javaWriter._

  def traverse(enumConstantsVal: Defn.Val): Unit = {
    val enumConstants = enumConstantsVal.pats.collect { case patVar: Pat.Var => patVar }
    val invalid = enumConstantsVal.pats.filterNot(_.isInstanceOf[Pat.Var])

    (enumConstants, invalid) match {
      case (Nil, _) => throw new IllegalStateException(s"No valid enum constants found in LHS of $enumConstantsVal")
      case (_, _ :: _) => throw new IllegalStateException(s"Invalid enum constants found in LHS of $enumConstantsVal")
      case (enumConsts, _) =>
        argumentListTraverser.traverse(
          args = enumConsts,
          argTraverser = (patVar: Pat.Var, _) => write(patVar.name.value)
        )
    }
  }
}
