package io.github.effiban.scala2java.contexts

import io.github.effiban.scala2java.entities.Decision.{Decision, No}

case class TryContext(override val shouldReturnValue: Decision = No) extends ShouldReturnValueIndicator
