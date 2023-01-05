package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.{Classifiers, TypeNameClassifier}
import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.transformers.TermToTupleCaster

class TypeInferrers(classifiers: => Classifiers)(implicit extensionRegistry: ExtensionRegistry) {

  private[typeinference] lazy val applyTypeInferrer = new CompositeApplyTypeInferrer(coreApplyTypeInferrer)

  private[typeinference] lazy val applyTypeTypeInferrer = new CompositeApplyTypeTypeInferrer(
    new CoreApplyTypeTypeInferrer(termTypeInferrer)
  )

  private[typeinference] lazy val blockTypeInferrer = new BlockTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val caseListTypeInferrer = new CaseListTypeInferrerImpl(caseTypeInferrer, CollectiveTypeInferrer)

  private[typeinference] lazy val caseTypeInferrer = new CaseTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val compositeArgListTypesInferrer = new CompositeArgListTypesInferrerImpl(
    scalarArgListTypeInferrer,
    tupleArgListTypesInferrer,
    classifiers.termTypeClassifier,
    TermToTupleCaster
  )

  private[typeinference] lazy val coreApplyTypeInferrer = new CoreApplyTypeInferrer(
    applyTypeTypeInferrer,
    termTypeInferrer,
    compositeArgListTypesInferrer,
    TypeNameClassifier
  )

  private[typeinference] lazy val ifTypeInferrer = new IfTypeInferrerImpl(termTypeInferrer)

  lazy val scalarArgListTypeInferrer = new ScalarArgListTypeInferrerImpl(
    termTypeInferrer,
    CollectiveTypeInferrer
  )

  lazy val termTypeInferrer: TermTypeInferrer = new TermTypeInferrerImpl(
    coreApplyTypeInferrer,
    applyTypeTypeInferrer,
    blockTypeInferrer,
    caseListTypeInferrer,
    ifTypeInferrer,
    LitTypeInferrer,
    new CompositeNameTypeInferrer(CoreNameTypeInferrer),
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
