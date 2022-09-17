package effiban.scala2java.contexts

import scala.meta.{Init, Term, Type}

case class CtorContext(className: Type.Name, inits: List[Init] = Nil, terms: List[Term] = Nil)
