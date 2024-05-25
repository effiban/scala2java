package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.desugarers.DifferentTypeDesugarer

import scala.meta.Term

trait TermApplyInfixDesugarer extends DifferentTypeDesugarer[Term.ApplyInfix, Term]
