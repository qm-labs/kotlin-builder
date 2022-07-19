package tech.qmachines.builder.annotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
@MutablePropertyType(
    MutableProperty(
        immutableClass = Map::class,
        mutableClass = Map::class,
        translateBuilders = TranslateBuilders.INFER,
        toMutableMethod = "%p.mapValues { it.value.builder() }.toMutableMap()",
        toImmutableMethod = "%p.mapValues { it.value.build() }"
    )
)
annotation class MutableBuilderMapProperty