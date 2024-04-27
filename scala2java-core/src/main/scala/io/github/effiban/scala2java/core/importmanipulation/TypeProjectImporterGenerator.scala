package io.github.effiban.scala2java.core.importmanipulation

import scala.meta.{Importee, Importer, Name, Term, Type}

trait TypeProjectImporterGenerator {
  def generate(typeProject: Type.Project): Option[Importer]
}

private[importmanipulation] class TypeProjectImporterGeneratorImpl(typeToTermRefConverter: TypeToTermRefConverter)
  extends TypeProjectImporterGenerator {

  override def generate(typeProject: Type.Project): Option[Importer] = {
    typeToTermRefConverter.toTermRefPath(typeProject.qual).map(ref =>
      Importer(
        ref = ref,
        importees = List(Importee.Name(Name.Indeterminate(typeProject.name.value)))
      )
    )
  }
}

object TypeProjectImporterGenerator extends TypeProjectImporterGeneratorImpl(TypeToTermRefConverter)
