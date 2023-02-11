package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.{TermApplyInfixClassifier, TypeNameClassifier}
import io.github.effiban.scala2java.core.extensions.ExtensionRegistry

class TypeInferrers(implicit extensionRegistry: ExtensionRegistry) {

  private[typeinference] lazy val applyInfixTypeInferrer = new ApplyInfixTypeInferrerImpl(tupleTypeInferrer, TermApplyInfixClassifier)

  private[typeinference] lazy val applyTypeInferrer = new CompositeApplyTypeInferrer(coreApplyTypeInferrer)

  private[typeinference] lazy val applyTypeTypeInferrer = new CompositeApplyTypeTypeInferrer(
    new CoreApplyTypeTypeInferrer(termTypeInferrer)
  )

  private[typeinference] lazy val blockTypeInferrer = new BlockTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val caseListTypeInferrer = new CaseListTypeInferrerImpl(caseTypeInferrer, CollectiveTypeInferrer)

  private[typeinference] lazy val caseTypeInferrer = new CaseTypeInferrerImpl(termTypeInferrer)

  lazy val compositeCollectiveTypeInferrer = new CompositeCollectiveTypeInferrerImpl(CollectiveTypeInferrer)

  private[typeinference] lazy val coreApplyTypeInferrer = new CoreApplyTypeInferrer(
    applyTypeTypeInferrer,
    termTypeInferrer,
    compositeCollectiveTypeInferrer,
    TypeNameClassifier
  )

  lazy val functionTypeInferrer = new FunctionTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val ifTypeInferrer = new IfTypeInferrerImpl(termTypeInferrer)

  lazy val termTypeInferrer: TermTypeInferrer = new TermTypeInferrerImpl(
    applyInfixTypeInferrer,
    applyTypeInferrer,
    applyTypeTypeInferrer,
    blockTypeInferrer,
    caseListTypeInferrer,
    functionTypeInferrer,
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

  private[typeinference] lazy val tupleTypeInferrer = new TupleTypeInferrerImpl(termTypeInferrer)
}
