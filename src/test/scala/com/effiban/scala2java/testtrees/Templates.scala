package com.effiban.scala2java.testtrees

import scala.meta.{Name, Self, Template}

object Templates {

  val Empty: Template = Template(
    early = Nil,
    inits = Nil,
    self = Self(name = Name.Anonymous(), decltpe = None),
    stats  = Nil
  )
}
