package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaTreeType.JavaTreeType

import scala.meta.{Init, Term, Type}

case class CtorContext(override val javaScope: JavaTreeType,
                       className: Type.Name,
                       inits: List[Init] = Nil,
                       terms: List[Term] = Nil) extends JavaScopeAware
