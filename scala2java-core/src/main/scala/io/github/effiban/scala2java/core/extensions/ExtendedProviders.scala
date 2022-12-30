package io.github.effiban.scala2java.core.extensions

import io.github.effiban.scala2java.spi.providers.AdditionalImportersProvider

private[extensions] trait ExtendedProviders { this: ExtensionContainer =>

  val additionalImportersProviders: List[AdditionalImportersProvider] = extensions.map(_.additionalImportersProvider())
}
