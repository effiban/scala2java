package io.github.effiban.scala2java.core.collectors

import scala.meta.{Defn, Init, Source}

object MainClassInitCollector extends SourceCollector[Init] {

  override def collect(source: Source): List[Init] =
    source.collect { case cls: Defn.Class => cls }
      .headOption
      .map(_.templ.inits)
      .getOrElse(Nil)
}
