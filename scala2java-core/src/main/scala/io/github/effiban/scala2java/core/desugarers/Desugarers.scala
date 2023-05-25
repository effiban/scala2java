package io.github.effiban.scala2java.core.desugarers

import scala.meta.{Source, Template}

class Desugarers() {

  private lazy val declDesugarer: DeclDesugarer = new DeclDesugarerImpl()

  private lazy val defnDefDesugarer: DefnDefDesugarer = new DefnDefDesugarerImpl(termParamDesugarer, evaluatedTermDesugarer)

  private lazy val defnDesugarer: DefnDesugarer = new DefnDesugarerImpl(
    defnDefDesugarer,
    defnObjectDesugarer,
    treeDesugarer)

  private lazy val defnObjectDesugarer: DefnObjectDesugarer = new DefnObjectDesugarerImpl(templateDesugarer)

  private lazy val evaluatedTermDesugarer: EvaluatedTermDesugarer = new EvaluatedTermDesugarerImpl(treeDesugarer)

  val sourceDesugarer: SameTypeDesugarer[Source] = new DefaultSameTypeDesugarer[Source](treeDesugarer)

  private lazy val statDesugarer: StatDesugarer = new StatDesugarerImpl(
    defnDesugarer,
    declDesugarer,
    evaluatedTermDesugarer,
    treeDesugarer
  )

  private lazy val templateDesugarer: SameTypeDesugarer[Template] = new DefaultSameTypeDesugarer[Template](treeDesugarer)

  private lazy val termParamDesugarer: TermParamDesugarer = new TermParamDesugarerImpl(evaluatedTermDesugarer)

  private lazy val treeDesugarer: TreeDesugarer = new TreeDesugarerImpl(statDesugarer, termParamDesugarer)
}
