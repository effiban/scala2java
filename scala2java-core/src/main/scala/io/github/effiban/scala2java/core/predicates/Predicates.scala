package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.predicates.TermNameSupportsNoArgInvocation;

class Predicates(implicit extensionRegistry: ExtensionRegistry) {

    lazy val compositeTermNameSupportsNoArgInvocation: TermNameSupportsNoArgInvocation =
        new CompositeTermNameSupportsNoArgInvocation(CoreTermNameSupportsNoArgInvocation)
}
