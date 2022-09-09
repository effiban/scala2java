package effiban.scala2java.contexts

import effiban.scala2java.entities.Decision.{Decision, No}

case class TryContext(override val shouldReturnValue: Decision = No) extends ShouldReturnValueIndicator
