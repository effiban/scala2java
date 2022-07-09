package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Importee

trait ImporteeTraverser extends ScalaTreeTraverser[Importee]

private[traversers] class ImporteeTraverserImpl(nameTraverser: => NameTraverser)
                                               (implicit javaWriter: JavaWriter) extends ImporteeTraverser {

  import javaWriter._

  // A single imported element within an Importer (can be one name, wildcard etc. see below)
  override def traverse(importee: Importee): Unit = {
    importee match {
      case Importee.Name(name) => nameTraverser.traverse(name)
      case Importee.Wildcard() => write("*")
      case Importee.Rename(name, rename) =>
        nameTraverser.traverse(name)
        write(" ")
        writeComment(s"Renamed in Scala to '${rename.toString}'")
      case Importee.Unimport(name) =>
        nameTraverser.traverse(name)
        write(" ")
        writeComment(s"Hidden (unimported) in Scala")
    }
  }
}

object ImporteeTraverser extends ImporteeTraverserImpl(NameTraverser)
