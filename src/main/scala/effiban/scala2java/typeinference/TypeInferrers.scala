package effiban.scala2java.typeinference

object TypeInferrers {

  private[typeinference] lazy val applyTypeTypeInferrer = new ApplyTypeTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val blockTypeInferrer = new BlockTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val caseListTypeInferrer = new CaseListTypeInferrerImpl(caseTypeInferrer, CollectiveTypeInferrer)

  private[typeinference] lazy val caseTypeInferrer = new CaseTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val ifTypeInferrer = new IfTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val tryTypeInferrer = new TryTypeInferrerImpl(
    termTypeInferrer,
    caseListTypeInferrer,
    CollectiveTypeInferrer
  )

  private[typeinference] lazy val tryWithHandlerTypeInferrer = new TryWithHandlerTypeInferrerImpl(termTypeInferrer, CollectiveTypeInferrer)

  lazy val termTypeInferrer: TermTypeInferrer = new TermTypeInferrerImpl(
    applyTypeTypeInferrer,
    blockTypeInferrer,
    caseListTypeInferrer,
    ifTypeInferrer,
    LitTypeInferrer,
    NameTypeInferrer,
    tryTypeInferrer,
    tryWithHandlerTypeInferrer
  )
}
