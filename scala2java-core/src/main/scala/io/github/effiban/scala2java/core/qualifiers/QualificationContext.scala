package io.github.effiban.scala2java.core.qualifiers

import scala.meta.{Importer, Type}

case class QualificationContext(importers: List[Importer] = Nil, qualifiedTypeMap: Map[Type, Type] = Map.empty)
