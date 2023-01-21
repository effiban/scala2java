package io.github.effiban.scala2java.core.extensions

private[extensions] trait ForcedExtensionNamesResolver {

  def resolve(): Set[String]
}

private[extensions] object ForcedExtensionNamesResolver extends ForcedExtensionNamesResolver {

  private final val ForcedExtensionsPropertyName = "Scala2JavaForcedExtensions"

  override def resolve(): Set[String] = {
    Option(System.getProperty(ForcedExtensionsPropertyName))
      .map(_.split(","))
      .map(extNames => extNames.map(_.trim).filterNot(_.isEmpty))
      .map(_.toSet)
      .getOrElse(Set.empty)
  }
}
