package effiban.scala2java.contexts

import effiban.scala2java.entities.Decision.{Decision, No}

import scala.meta.Init

// The 'init' param is passed by constructors, whose first statement must be a call to super or other ctor.
// 'Init' does not inherit from 'Stat' so we can't add it to the Block
// TODO - when expecting a return value which is a lambda, need another flag for returnability inside the lambda body
case class BlockContext(override val shouldReturnValue: Decision = No,
                        maybeInit: Option[Init] = None) extends ShouldReturnValueIndicator
