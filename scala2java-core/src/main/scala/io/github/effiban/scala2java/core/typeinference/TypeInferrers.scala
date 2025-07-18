package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.classifiers.{CompositeTypeClassifier, TermApplyInfixClassifier}
import io.github.effiban.scala2java.core.entities.ParameterizedInitializerNameTypeMapping
import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.core.factories.Factories
import io.github.effiban.scala2java.core.predicates._
import io.github.effiban.scala2java.core.reflection.{ScalaReflectionMethodSignatureInferrer, ScalaReflectionTypeInferrer}

class TypeInferrers(factories: => Factories)
                   (implicit extensionRegistry: ExtensionRegistry) {

  private[typeinference] lazy val applyInfixTypeInferrer = new ApplyInfixTypeInferrerImpl(tupleTypeInferrer, TermApplyInfixClassifier)

  lazy val applyParentTypeInferrer = new ApplyParentTypeInferrerImpl(qualifierTypeInferrer)

  private[typeinference] lazy val applyReturnTypeInferrer = new ApplyReturnTypeInferrerImpl(
    factories.termApplyInferenceContextFactory,
    internalApplyDeclDefInferrer
  )

  private[typeinference] lazy val applyTypeTypeInferrer = new ApplyTypeTypeInferrerImpl(applyReturnTypeInferrer)

  private[typeinference] lazy val blockTypeInferrer = new BlockTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val caseListTypeInferrer = new CaseListTypeInferrerImpl(caseTypeInferrer, CollectiveTypeInferrer)

  private[typeinference] lazy val caseTypeInferrer = new CaseTypeInferrerImpl(termTypeInferrer)

  lazy val compositeCollectiveTypeInferrer = new CompositeCollectiveTypeInferrerImpl(CollectiveTypeInferrer)

  private[typeinference] lazy val coreApplyDeclDefInferrer = new CoreApplyDeclDefInferrer(
    parameterizedInitializerDeclDefInferrer,
    CompositeTypeClassifier
  )

  lazy val functionTypeInferrer = new FunctionTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val ifTypeInferrer = new IfTypeInferrerImpl(termTypeInferrer)

  lazy val internalApplyDeclDefInferrer: InternalApplyDeclDefInferrer = new InternalApplyDeclDefInferrerImpl(
    new CompositeApplyDeclDefInferrer(coreApplyDeclDefInferrer),
    TermSelectHasApplyMethod,
    ScalaReflectionMethodSignatureInferrer
  )

  private[typeinference] lazy val internalSelectTypeInferrer = new InternalSelectTypeInferrerImpl(
    applyReturnTypeInferrer,
    qualifierTypeInferrer,
    new CompositeTermSelectSupportsNoArgInvocation(CoreTermSelectSupportsNoArgInvocation),
    ScalaReflectionTypeInferrer
  )

  private lazy val parameterizedInitializerDeclDefInferrer = new InitializerDeclDefInferrerImpl(
    compositeCollectiveTypeInferrer,
    ParameterizedInitializerNameTypeMapping
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
    internalSelectTypeInferrer,
    SuperTypeInferrer,
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
