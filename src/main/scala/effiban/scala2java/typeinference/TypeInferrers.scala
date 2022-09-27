package effiban.scala2java.typeinference

import effiban.scala2java.classifiers.TypeNameClassifier

object TypeInferrers {

  private[typeinference] lazy val applyTypeInferrer = new ApplyTypeInferrerImpl(
    applyTypeTypeInferrer,
    termTypeInferrer,
    termArgsToTypeArgsInferrer,
    TypeNameClassifier)

  private[typeinference] lazy val applyTypeTypeInferrer = new ApplyTypeTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val blockTypeInferrer = new BlockTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val caseListTypeInferrer = new CaseListTypeInferrerImpl(caseTypeInferrer, CollectiveTypeInferrer)

  private[typeinference] lazy val caseTypeInferrer = new CaseTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val ifTypeInferrer = new IfTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val termArgsToTypeArgsInferrer = new TermArgsToTypeArgsInferrerImpl(
    termTypeInferrer,
    tupleTypeInferrer,
    CollectiveTypeInferrer
  )

  lazy val termTypeInferrer: TermTypeInferrer = new TermTypeInferrerImpl(
    applyTypeInferrer,
    applyTypeTypeInferrer,
    blockTypeInferrer,
    caseListTypeInferrer,
    ifTypeInferrer,
    LitTypeInferrer,
    NameTypeInferrer,
    tryTypeInferrer,
    tryWithHandlerTypeInferrer,
    tupleTypeInferrer
  )

  private[typeinference] lazy val tryTypeInferrer = new TryTypeInferrerImpl(
    termTypeInferrer,
    caseListTypeInferrer,
    CollectiveTypeInferrer
  )

  private[typeinference] lazy val tryWithHandlerTypeInferrer = new TryWithHandlerTypeInferrerImpl(termTypeInferrer, CollectiveTypeInferrer)

  private[typeinference] lazy val tupleTypeInferrer = new TupleTypeInferrerImpl(termTypeInferrer)
}
