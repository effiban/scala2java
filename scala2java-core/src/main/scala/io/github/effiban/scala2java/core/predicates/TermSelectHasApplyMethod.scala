package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.reflection.ScalaReflectionUtils.{isTermMemberOf, isTermMemberOfCompanionOf}

import scala.meta.{Term, Type, XtensionQuasiquoteTerm}

// TODO - once file-scope or external type inference is added, add ability to check by a given type as well
trait TermSelectHasApplyMethod extends (Term.Select => Boolean)

object TermSelectHasApplyMethod extends TermSelectHasApplyMethod {

  override def apply(termSelect: Term.Select): Boolean = {
    (termSelect.qual, termSelect.name) match {
      case (qual: Term.Ref, name) =>
        hasApplyMethodOnObjectOf(termSelect) ||
        hasApplyMethodOnCompanionObjectOfType(Type.Select(qual, Type.Name(name.value)))
      case _ => false
    }
  }

  private def hasApplyMethodOnObjectOf(termSelect: Term.Select) = {
    isTermMemberOf(termSelect, q"apply")
  }

  private def hasApplyMethodOnCompanionObjectOfType(typeSelect: Type.Select) = {
    // This is a "smart hack".
    // Sometimes the code will define a variable referencing a Scala object (type singleton), and we cannot resolve that by reflection.
    // However usually the object in question is a companion object, and its associated type also has an alias
    // with the same qualified name. That alias can be resolved by reflection, so we can use it to reach the desired object.
    // Example: "scala.List" refers to "scala.collection.immutable.List" for both the class and the object.
    isTermMemberOfCompanionOf(typeSelect, q"apply")
  }
}
