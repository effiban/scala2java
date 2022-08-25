package effiban.scala2java.typeinference

object TypeInferrers {

  private[typeinference] lazy val ifTypeInferrer = new IfTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val blockTypeInferrer = new BlockTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val caseTypeInferrer = new CaseTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val caseListTypeInferrer = new CaseListTypeInferrerImpl(caseTypeInferrer, CollectiveTypeInferrer)

  lazy val termTypeInferrer: TermTypeInferrer = new TermTypeInferrerImpl(
    ifTypeInferrer,
    blockTypeInferrer,
    LitTypeInferrer
  )
}
