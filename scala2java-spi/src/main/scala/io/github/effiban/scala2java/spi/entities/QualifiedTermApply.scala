package io.github.effiban.scala2java.spi.entities

import scala.meta.{Term, Type}

/** Holds a qualified [[Term.Apply]] (method invocation), which means that it holds the components of a [[Term.Apply]]
 * separately, including the qualified name as a [[Term.Select]] instead of the generic [[Term]].<br>
 * This is done to achieve a clear and focused API for the corresponding transformer so clients will understand
 * how to use it.
 *
 * @param qualifiedName the qualified method name
 * @param typeArgs the method type arguments, when exist
 * @param args the method arguments, when exist
 */
case class QualifiedTermApply(qualifiedName: Term.Select,
                              typeArgs: List[Type] = Nil,
                              args: List[Term] = Nil) {

  def asUnqualified(): UnqualifiedTermApply = UnqualifiedTermApply(qualifiedName.name, typeArgs, args)

  def asTermApply(): Term.Apply = typeArgs match {
    case Nil => Term.Apply(qualifiedName, args)
    case targs => Term.Apply(Term.ApplyType(qualifiedName, targs), args)
  }
}

object QualifiedTermApply {
  def apply(qualifiedName: Term.Select, args: List[Term]): QualifiedTermApply =
    new QualifiedTermApply(qualifiedName = qualifiedName, args = args)
}

