package io.github.effiban.scala2java.core.providers

import io.github.effiban.scala2java.core.entities.TermNameValues.{Java, Util}
import io.github.effiban.scala2java.spi.providers.JavaImportersProvider

import scala.meta.Importee.Wildcard
import scala.meta.Term.Select
import scala.meta.{Importer, Term}

object DefaultJavaImportersProvider extends JavaImportersProvider {

  private val importers =
    List(
      Importer(Select(Term.Name(Java), Term.Name("io")), List(Wildcard())),
      Importer(Select(Term.Name(Java), Term.Name("lang")), List(Wildcard())),
      Importer(Select(Term.Name(Java), Term.Name("math")), List(Wildcard())),
      Importer(Select(Term.Name(Java), Term.Name(Util)), List(Wildcard())),
      Importer(Select(Select(Term.Name(Java), Term.Name(Util)), Term.Name("function")), List(Wildcard())),
      Importer(Select(Select(Term.Name(Java), Term.Name(Util)), Term.Name("stream")), List(Wildcard()))
    )

  override def provide(): List[Importer] = importers
}
