package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.{TermApplyInfixClassifier, TypeNameClassifier}
import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.factories.Factories
import io.github.effiban.scala2java.spi.typeinferrers.ApplyDeclDefInferrer

class TypeInferrers(factories: => Factories)(implicit extensionRegistry: ExtensionRegistry) {

  lazy val applyDeclDefInferrer: ApplyDeclDefInferrer =
    new CompositeApplyDeclDefInferrer(coreApplyDeclDefInferrer)

  private[typeinference] lazy val applyInfixTypeInferrer = new ApplyInfixTypeInferrerImpl(tupleTypeInferrer, TermApplyInfixClassifier)

  lazy val applyParentTypeInferrer = new ApplyParentTypeInferrerImpl(qualifierTypeInferrer)

  private[typeinference] lazy val applyReturnTypeInferrer = new ApplyReturnTypeInferrerImpl(factories.termApplyInferenceContextFactory, applyDeclDefInferrer)

  private[typeinference] lazy val applyTypeTypeInferrer = new CompositeApplyTypeTypeInferrer(
    new CoreApplyTypeTypeInferrer(termTypeInferrer)
  )

  private[typeinference] lazy val blockTypeInferrer = new BlockTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val caseListTypeInferrer = new CaseListTypeInferrerImpl(caseTypeInferrer, CollectiveTypeInferrer)

  private[typeinference] lazy val caseTypeInferrer = new CaseTypeInferrerImpl(termTypeInferrer)

  lazy val compositeCollectiveTypeInferrer = new CompositeCollectiveTypeInferrerImpl(CollectiveTypeInferrer)

  private[typeinference] lazy val coreApplyDeclDefInferrer = new CoreApplyDeclDefInferrer(
    applyTypeTypeInferrer,
    termTypeInferrer,
    compositeCollectiveTypeInferrer,
    TypeNameClassifier
  )

  lazy val functionTypeInferrer = new FunctionTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val ifTypeInferrer = new IfTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val internalSelectTypeInferrer = new InternalSelectTypeInferrerImpl(
    qualifierTypeInferrer,
    new CompositeSelectTypeInferrer(CoreSelectTypeInferrer)
  )

  lazy val qualifierTypeInferrer = new QualifierTypeInferrerImpl(termTypeInferrer)

  lazy val termTypeInferrer: TermTypeInferrer = new TermTypeInferrerImpl(
    applyInfixTypeInferrer,
    applyReturnTypeInferrer,
    applyTypeTypeInferrer,
    blockTypeInferrer,
    caseListTypeInferrer,
    functionTypeInferrer,
    ifTypeInferrer,
    LitTypeInferrer,
    new CompositeNameTypeInferrer(CoreNameTypeInferrer),
    internalSelectTypeInferrer,
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
