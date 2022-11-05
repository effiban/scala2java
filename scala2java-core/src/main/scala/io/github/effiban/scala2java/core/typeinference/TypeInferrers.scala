package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.Classifiers.termTypeClassifier
import io.github.effiban.scala2java.core.classifiers.TypeNameClassifier
import io.github.effiban.scala2java.core.transformers.TermToTupleCaster

object TypeInferrers {

  private[typeinference] lazy val applyTypeInferrer = new ApplyTypeInferrerImpl(
    applyTypeTypeInferrer,
    termTypeInferrer,
    compositeArgListTypesInferrer,
    TypeNameClassifier)

  private[typeinference] lazy val applyTypeTypeInferrer = new ApplyTypeTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val blockTypeInferrer = new BlockTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val caseListTypeInferrer = new CaseListTypeInferrerImpl(caseTypeInferrer, CollectiveTypeInferrer)

  private[typeinference] lazy val caseTypeInferrer = new CaseTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val compositeArgListTypesInferrer = new CompositeArgListTypesInferrerImpl(
    scalarArgListTypeInferrer,
    tupleArgListTypesInferrer,
    termTypeClassifier,
    TermToTupleCaster
  )

  private[typeinference] lazy val ifTypeInferrer = new IfTypeInferrerImpl(termTypeInferrer)

  lazy val scalarArgListTypeInferrer = new ScalarArgListTypeInferrerImpl(
    termTypeInferrer,
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
    SelectTypeInferrer,
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

  private[typeinference] lazy val tupleArgListTypesInferrer = new TupleArgListTypesInferrerImpl(
    tupleTypeInferrer,
    CollectiveTypeInferrer
  )

  private[typeinference] lazy val tupleTypeInferrer = new TupleTypeInferrerImpl(termTypeInferrer)
}
