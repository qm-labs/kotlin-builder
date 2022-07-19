package tech.qmachines.builder.annotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
@MutablePropertyType(
    MutableProperty(
        immutableClass = Map::class,
        mutableClass = MutableMap::class,
        translateBuilders = TranslateBuilders.TYPE_AND_TYPE_ARGUMENTS,
        toMutableMethod = "%p.toMutableMap()"
    )
)
annotation class MutableMapProperty