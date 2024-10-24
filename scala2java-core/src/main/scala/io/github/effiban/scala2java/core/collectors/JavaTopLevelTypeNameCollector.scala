package io.github.effiban.scala2java.core.collectors

import scala.meta.Defn.Trait
import scala.meta.{Defn, Member, Name, Pkg, Source}

object JavaTopLevelTypeNameCollector extends SourceCollector[Name] {

  override def collect(source: Source): List[Name] = {
    val topLevelContainer = source.collect { case pkg: Pkg => pkg }
      .headOption
      .getOrElse(source)

    topLevelContainer.children.collect {
        case topLevelType@(_: Trait | _: Defn.Class | _: Defn.Object) =>
          topLevelType.asInstanceOf[Member].name
      }
  }
}
