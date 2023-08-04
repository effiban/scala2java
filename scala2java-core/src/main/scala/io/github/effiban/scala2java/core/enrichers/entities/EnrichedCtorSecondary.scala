package io.github.effiban.scala2java.core.enrichers.entities

import io.github.effiban.scala2java.core.entities.JavaModifier

import scala.meta.{Ctor, Type}

case class EnrichedCtorSecondary(stat: Ctor.Secondary,
                                 className: Type.Name,
                                 javaModifiers: List[JavaModifier] = Nil) extends EnrichedStatWithJavaModifiers
