package com.effiban.scala2java.transformers

import scala.meta.{Term, Type}

trait TypeSingletonTransformer {
  def transform(singletonType: Type.Singleton): Term
}

private[scala2java] class TypeSingletonTransformerImpl extends TypeSingletonTransformer {

  // A scala expression representing the singleton type of a term, e.g.: A.type
  override def transform(singletonType: Type.Singleton): Term = {
    singletonType.ref match {
      case `this`: Term.This => `this`
      //TODO This will only work for Java objects - support Java primitives as well by boxing
      case ref => Term.Apply(fun = Term.Select(ref, Term.Name("getClass")), args = Nil)
    }
  }
}

object TypeSingletonTransformer extends TypeSingletonTransformerImpl