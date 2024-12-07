package io.github.effiban.scala2java.core.cleanup

import scala.meta.{Template, Term, Type}

object IsTemplateAncestorUsedByCtorInvocation extends IsTemplateAncestorUsed {

  def apply(template: Template, ancestorType: Type.Ref): Boolean = {
    // Any call to the superclass ctor. would have been converted before this point to a Java-style call to a method named "super"
    template.collect {
        case termApply@Term.Apply(Term.Name("super"), _) => termApply
      }.nonEmpty
  }
}
