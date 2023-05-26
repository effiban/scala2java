package io.github.effiban.scala2java.core.desugarers

import io.github.effiban.scala2java.core.predicates.Predicates

import scala.meta.{Source, Template}

class Desugarers(implicit predicates: Predicates) {
  import predicates._

  private lazy val declDefDesugarer: DeclDefDesugarer = new DeclDefDesugarerImpl(termParamDesugarer)

  private lazy val declDesugarer: DeclDesugarer = new DeclDesugarerImpl(declDefDesugarer)

  private lazy val defnDefDesugarer: DefnDefDesugarer = new DefnDefDesugarerImpl(termParamDesugarer, evaluatedTermDesugarer)

  private lazy val defnDesugarer: DefnDesugarer = new DefnDesugarerImpl(
    defnDefDesugarer,
    defnObjectDesugarer,
    treeDesugarer)

  private lazy val defnObjectDesugarer: DefnObjectDesugarer = new DefnObjectDesugarerImpl(templateDesugarer)

  private lazy val evaluatedTermDesugarer: EvaluatedTermDesugarer = new EvaluatedTermDesugarerImpl(evaluatedTermRefDesugarer, treeDesugarer)

  private lazy val evaluatedTermNameDesugarer: EvaluatedTermNameDesugarer = new EvaluatedTermNameDesugarerImpl(
    compositeTermNameSupportsNoArgInvocation
  )

  private lazy val evaluatedTermRefDesugarer: EvaluatedTermRefDesugarer = new EvaluatedTermRefDesugarerImpl(
    evaluatedTermNameDesugarer,
    treeDesugarer
  )

  private lazy val pkgDesugarer: PkgDesugarer = new PkgDesugarerImpl(statDesugarer)

  val sourceDesugarer: SameTypeDesugarer[Source] = new DefaultSameTypeDesugarer[Source](treeDesugarer)

  private lazy val statDesugarer: StatDesugarer = new StatDesugarerImpl(
    pkgDesugarer,
    defnDesugarer,
    declDesugarer,
    evaluatedTermDesugarer,
    treeDesugarer
  )

  private lazy val templateDesugarer: SameTypeDesugarer[Template] = new DefaultSameTypeDesugarer[Template](treeDesugarer)

  private lazy val termParamDesugarer: TermParamDesugarer = new TermParamDesugarerImpl(evaluatedTermDesugarer)

  private lazy val treeDesugarer: TreeDesugarer = new TreeDesugarerImpl(statDesugarer, termParamDesugarer)
}
