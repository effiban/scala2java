package effiban.scala2java.transformers

import scala.meta.{Init, Term}

trait TemplateInitsToSuperCallTransformer {
  def transform(inits: List[Init] = Nil): Option[Term.Apply]
}

object TemplateInitsToSuperCallTransformer extends TemplateInitsToSuperCallTransformer {

  override def transform(inits: List[Init]): Option[Term.Apply] = {
    // In Java only one superclass can be called by super(), so taking the first one with arguments arbitrarily
    inits.filter(_.argss.nonEmpty)
      .map(originalInit => Term.Apply(fun = Term.Name("super"), args = originalInit.argss.flatten))
      .headOption
  }
}


