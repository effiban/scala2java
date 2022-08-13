package effiban.scala2java.typeinference

object TypeInferrers {

  private[typeinference] lazy val ifTypeInferrer: IfTypeInferrer = new IfTypeInferrerImpl(termTypeInferrer)

  private[typeinference] lazy val blockTypeInferrer: BlockTypeInferrer = new BlockTypeInferrerImpl(termTypeInferrer)

  lazy val termTypeInferrer: TermTypeInferrer = new TermTypeInferrerImpl(
    ifTypeInferrer,
    blockTypeInferrer,
    LitTypeInferrer
  )
}
