package io.github.effiban.scala2java.core.desugarers.semantic

import io.github.effiban.scala2java.core.desugarers.SameTypeDesugarer
import io.github.effiban.scala2java.core.predicates.{Predicates, TermSelectHasApplyMethod}
import io.github.effiban.scala2java.core.typeinference.TypeInferrers

import scala.meta.{Source, Template}

class SemanticDesugarers(implicit predicates: Predicates,
                         typeInferrers: TypeInferrers) {

  import predicates._
  import typeInferrers._

  private lazy val applyUnaryDesugarer: ApplyUnaryDesugarer = new ApplyUnaryDesugarerImpl(evaluatedTermDesugarer)

  private lazy val assignDesugarer: AssignDesugarer = new AssignDesugarerImpl(evaluatedTermDesugarer)

  private lazy val declDefDesugarer: DeclDefDesugarer = new DeclDefDesugarerImpl(termParamDesugarer)

  private lazy val declDesugarer: DeclDesugarer = new DeclDesugarerImpl(declDefDesugarer)

  private lazy val defnDefDesugarer: DefnDefDesugarer = new DefnDefDesugarerImpl(termParamDesugarer, evaluatedTermDesugarer)

  private lazy val defnDesugarer: DefnDesugarer = new DefnDesugarerImpl(
    defnDefDesugarer,
    defnObjectDesugarer,
    treeDesugarer)

  private lazy val defnObjectDesugarer: DefnObjectDesugarer = new DefnObjectDesugarerImpl(templateDesugarer)

  private lazy val etaDesugarer: EtaDesugarer = new EtaDesugarerImpl(
    evaluatedTermSelectQualDesugarer,
    termApplyTypeFunDesugarer,
    evaluatedTermDesugarer
  )

  private lazy val evaluatedTermDesugarer: EvaluatedTermDesugarer = new EvaluatedTermDesugarerImpl(
    evaluatedTermRefDesugarer,
    termApplyDesugarer,
    termApplyTypeDesugarer,
    termApplyInfixDesugarer,
    assignDesugarer,
    etaDesugarer,
    treeDesugarer)

  private lazy val evaluatedTermRefDesugarer: EvaluatedTermRefDesugarer = new EvaluatedTermRefDesugarerImpl(
    evaluatedTermSelectDesugarer,
    applyUnaryDesugarer,
    treeDesugarer
  )

  private lazy val evaluatedTermSelectQualDesugarer = new EvaluatedTermSelectQualDesugarerImpl(evaluatedTermDesugarer)

  private lazy val evaluatedTermSelectDesugarer: EvaluatedTermSelectDesugarer = new EvaluatedTermSelectDesugarerImpl(
    qualifierTypeInferrer,
    compositeTermSelectSupportsNoArgInvocation,
    evaluatedTermSelectQualDesugarer
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

  private lazy val termApplyDesugarer: TermApplyDesugarer = new TermApplyDesugarerImpl(termApplyFunDesugarer, evaluatedTermDesugarer)

  private lazy val termApplyFunDesugarer: TermApplyFunDesugarer = new TermApplyFunDesugarerImpl(
    TermSelectHasApplyMethod,
    evaluatedTermSelectQualDesugarer,
    termApplyTypeFunDesugarer,
    evaluatedTermDesugarer
  )

  private lazy val termApplyInfixDesugarer: TermApplyInfixDesugarer = new TermApplyInfixDesugarerImpl(evaluatedTermDesugarer)

  private lazy val termApplyTypeFunDesugarer: TermApplyTypeFunDesugarer = new TermApplyTypeFunDesugarerImpl(
    evaluatedTermSelectQualDesugarer,
    evaluatedTermDesugarer
  )

  private lazy val termApplyTypeDesugarer: TermApplyTypeDesugarer = new TermApplyTypeDesugarerImpl(termApplyDesugarer)

  private lazy val termParamDesugarer: TermParamDesugarer = new TermParamDesugarerImpl(evaluatedTermDesugarer)

  private lazy val treeDesugarer: TreeDesugarer = new TreeDesugarerImpl(statDesugarer, termParamDesugarer)
}
