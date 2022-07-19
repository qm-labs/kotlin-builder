package tech.qmachines.builder.annotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
@MutablePropertyType(
    MutableProperty(
        immutableClass = List::class,
        mutableClass = MutableList::class,
        translateBuilders = TranslateBuilders.TYPE_AND_TYPE_ARGUMENTS,
        toMutableMethod = "%p.toMutableList()"
    )
)
annotation class MutableListProperty