package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.SameTypeDesugarer

import scala.meta.Defn

trait DefnDefDesugarer extends SameTypeDesugarer[Defn.Def]

private[semantic] class DefnDefDesugarerImpl(termParamDesugarer: => TermParamDesugarer,
                                               evaluatedTermDesugarer: => EvaluatedTermDesugarer) extends DefnDefDesugarer {

  override def desugar(defnDef: Defn.Def): Defn.Def = {
    import defnDef._

    val desugaredParamss = defnDef.paramss.map(_.map(termParamDesugarer.desugar))
    val desugaredBody = evaluatedTermDesugarer.desugar(body)

    defnDef.copy(paramss = desugaredParamss, body = desugaredBody)
  }
}
