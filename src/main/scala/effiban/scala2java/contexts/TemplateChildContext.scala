package effiban.scala2java.contexts

import effiban.scala2java.entities.JavaScopeModifier.JavaScopeModifier
import effiban.scala2java.entities.JavaTreeType.JavaTreeType

import scala.meta.{Init, Term, Type}

case class TemplateChildContext(override val javaScope: JavaTreeType,
                                override val javaScopeModifiers: Set[JavaScopeModifier] = Set.empty,
                                maybeClassName: Option[Type.Name] = None,
                                inits: List[Init] = Nil,
                                ctorTerms: List[Term] = Nil) extends JavaScopeAware
