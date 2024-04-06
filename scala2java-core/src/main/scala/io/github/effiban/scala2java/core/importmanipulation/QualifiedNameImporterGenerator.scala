package io.github.effiban.scala2java.core.importmanipulation

import io.github.effiban.scala2java.core.reflection.JavaReflectionUtils.{classForName, staticFieldFor, staticMethodFor}

import scala.meta.{Importee, Importer, Name, Term, Type, XtensionParseInputLike}

trait QualifiedNameImporterGenerator {

  def generateForType(qual: Term.Ref, name: String): Option[Importer]

  def generateForStaticMethod(qual: Term.Ref, name: String, args: List[Term]): Option[Importer]

  def generateForStaticField(qual: Term.Ref, name: String): Option[Importer]
}

object QualifiedNameImporterGenerator extends QualifiedNameImporterGenerator {

  override def generateForType(qual: Term.Ref, name: String): Option[Importer] = {
    // TODO handle type which is child of static field
    classForName(Type.Select(qual, Type.Name(name)).toString())
      .map(cls => asImporter(cls.getPackageName, cls.getSimpleName))
  }

  override def generateForStaticMethod(qual: Term.Ref, name: String, args: List[Term]): Option[Importer] = {
    classForName(qual.toString()) match {
      // TODO improve accuracy by inferring method arg types
      case Some(tpe) =>
        staticMethodFor(tpe, name, args.length).map(
          method => asImporter(method.getDeclaringClass.getCanonicalName, method.getName)
        )
      case _ => None
    }
  }

  override def generateForStaticField(qual: Term.Ref, name: String): Option[Importer] = {
    classForName(qual.toString()) match {
      case Some(tpe) =>
        staticFieldFor(tpe, name).map(field => asImporter(field.getDeclaringClass.getCanonicalName, field.getName))
      case _ => None
    }
  }

  private def asImporter(qual: String, name: String) = {
    Importer(ref = qual.parse[Term].get.asInstanceOf[Term.Ref],
      importees = List(Importee.Name(Name.Indeterminate(name))))
  }
}

