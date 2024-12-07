package io.github.effiban.scala2java.core.cleanup

import scala.meta.{Template, Type}

private[cleanup] class CompositeIsTemplateAncestorUsed(predicates: List[IsTemplateAncestorUsed])
  extends IsTemplateAncestorUsed {

  def apply(template: Template, ancestorType: Type.Ref): Boolean = predicates.exists(_.apply(template, ancestorType))
}

object CompositeIsTemplateAncestorUsed extends CompositeIsTemplateAncestorUsed(
  List(
    IsTemplateAncestorUsedByAbstractEnclosingType,
    IsTemplateAncestorUsedByQualification,
    IsTemplateAncestorUsedByOverride,
    IsTemplateAncestorUsedByCtorInvocation,
    IsTemplateAncestorUsedByEmptiness
    // TODO add a predicate to indicate usage by ancestor initialization statements (cannot be done by reflection).
    //      It could cause an edge case where the resulting Java type will be missing an ancestor, but unlikely because:
    //      - A Java class cannot extend a Scala trait, and any initialization in the trait must be translated manually beforehand
    //      - If the ancestor is a class, it will almost always be included due to one of the other predicates
  )
)