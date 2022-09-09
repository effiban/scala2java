package effiban.scala2java.contexts

import effiban.scala2java.entities.Decision.{Decision, No}

case class CatchHandlerContext(override val shouldReturnValue: Decision = No) extends ShouldReturnValueIndicator
