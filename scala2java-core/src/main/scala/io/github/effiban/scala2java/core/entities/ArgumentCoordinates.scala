package io.github.effiban.scala2java.core.entities

import scala.meta.{Term, Tree}

case class ArgumentCoordinates(parent: Tree,
                               maybeName: Option[Term.Name] = None,
                               index: Int)
