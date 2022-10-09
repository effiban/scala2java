package io.github.effiban.scala2java.entities

object Decision extends Enumeration {
  type Decision = Value

  val Yes, No, Uncertain = Value

  //noinspection LanguageFeature
  implicit def boolean2Decision(b: Boolean): Decision = if (b) Yes else No
}

