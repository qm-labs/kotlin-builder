package tech.qmachines.builder.example

import tech.qmachines.builder.annotation.WithBuilder

@WithBuilder
class AnotherExample(builder: Builder) : BaseAnotherExample(builder) {

    class Builder(
        var value: Int = 0,
    ) : BaseAnotherExample.Builder()
}