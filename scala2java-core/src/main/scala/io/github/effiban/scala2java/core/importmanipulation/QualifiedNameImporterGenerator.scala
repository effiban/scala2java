package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.reflection.JavaReflectionUtils.classForName

import java.lang.reflect.Modifier.isStatic
import scala.meta.{Importee, Importer, Name, Term, Type}
import scala.util.Success

trait QualifiedNameImporterGenerator {

  def generateForType(qual: Term.Ref, name: String): Option[Importer]
  def generateForStaticMethod(qual: Term.Ref, name: String, args: List[Term]): Option[Importer]
  def generateForStaticField(qual: Term.Ref, name: String): Option[Importer]
}

object QualifiedNameImporterGenerator extends QualifiedNameImporterGenerator {

  override def generateForType(qual: Term.Ref, name: String): Option[Importer] =
    classForName(Type.Select(qual, Type.Name(name)).toString()) match {
      case Success(_) => asImporter(qual, name)
      case _ => None
    }

  override def generateForStaticMethod(qual: Term.Ref, name: String, args: List[Term]): Option[Importer] = {
    // TODO improve accuracy by inferring method arg types
    classForName(qual.toString())
      .map(_.getMethods)
      .map(_.filter(_.getName == name))
      .map(_.filter(method => isStatic(method.getModifiers)))
      .map(_.exists(method => method.getParameterCount == args.length)) match {

      case Success(true) => asImporter(qual, name)
      case _ => None
    }
  }

  override def generateForStaticField(qual: Term.Ref, name: String): Option[Importer] =
    classForName(qual.toString())
      .map(_.getField(name))
      .map(field => isStatic(field.getModifiers)) match {

      case Success(true) => asImporter(qual, name)
      case _ => None
    }

  private def asImporter(qual: Term.Ref, name: String) = {
    Some(Importer(ref = qual, importees = List(Importee.Name(Name.Indeterminate(name)))))
  }
}

