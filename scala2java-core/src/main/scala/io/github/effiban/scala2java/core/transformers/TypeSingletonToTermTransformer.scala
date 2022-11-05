package io.github.effiban.scala2java.core.transformers

import scala.meta.{Term, Type}

trait TypeSingletonToTermTransformer {
  def transform(singletonType: Type.Singleton): Term
}

object TypeSingletonToTermTransformer extends TypeSingletonToTermTransformer {

  // A scala expression representing the single type of a term, e.g.: A.type
  override def transform(singletonType: Type.Singleton): Term = {
    singletonType.ref match {
      case `this`: Term.This => `this`
      //TODO This will only work for Java objects - support Java primitives as well by boxing
      case ref => Term.Apply(fun = Term.Select(ref, Term.Name("getClass")), args = Nil)
    }
  }
}
