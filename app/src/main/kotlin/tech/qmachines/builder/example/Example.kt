package tech.qmachines.builder.example

import tech.qmachines.builder.annotation.*

@WithBuilder
class Example(builder: Builder) : BaseExample(builder) {


    class Builder(
        var value: Int = 0,
        @MutableListProperty val list: MutableList<String> = mutableListOf(),
        @MutableSetProperty val set: MutableSet<String> = mutableSetOf(),
        @MutableMapProperty val map: MutableMap<Int, String> = mutableMapOf(),
        @BuilderProperty val another: AnotherExample.Builder = AnotherExample.Builder(),
        @MutableBuilderMapProperty val builderMap: MutableMap<Int, AnotherExample.Builder> = mutableMapOf(),
        @ImmutableProperty val constant: String = "hello"
    ) : BaseExample.Builder()
}