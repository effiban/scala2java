package effiban.scala2java.typeinference

import scala.meta.{Case, Type}

trait CaseTypeInferrer extends TypeInferrer[Case]

private[typeinference] class CaseTypeInferrerImpl(termTypeInferrer: => TermTypeInferrer) extends CaseTypeInferrer {

  override def infer(`case`: Case): Option[Type] = termTypeInferrer.infer(`case`.body)
}