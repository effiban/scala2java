package io.github.effiban.scala2java.spi.entities

import scala.meta.{Term, Type}

/** Holds an unqualified [[Term.Apply]] (method invocation), which means that for a given method invocation which has a qualified name,
 * this class will hold all of the information except for the qualifier part of the method name.<br>
 * This is needed for managing the transformation of the name part when it depends only on the qualifier <b>type</b>,
 * while separately transforming the qualifier itself in a recursive call.
 *
 * @param name the unqualified method name
 * @param typeArgs the method type arguments, when exist
 * @param args the method arguments, when exist
 */
case class UnqualifiedTermApply(name: Term.Name,
                                typeArgs: List[Type] = Nil,
                                args: List[Term] = Nil) {

  def qualifiedBy(qualifier: Term): QualifiedTermApply = QualifiedTermApply(Term.Select(qualifier, name), typeArgs, args)
}

object UnqualifiedTermApply {
  def apply(name: Term.Name, args: List[Term]): UnqualifiedTermApply = new UnqualifiedTermApply(name = name, args = args)
}
