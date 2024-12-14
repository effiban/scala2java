package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.extensions.ExtensionRegistry
import io.github.effiban.scala2java.spi.predicates.TermSelectSupportsNoArgInvocation

class Predicates(implicit extensionRegistry: ExtensionRegistry) {

    lazy val compositeTermSelectSupportsNoArgInvocation: TermSelectSupportsNoArgInvocation =
        new CompositeTermSelectSupportsNoArgInvocation(CoreTermSelectSupportsNoArgInvocation)
}
