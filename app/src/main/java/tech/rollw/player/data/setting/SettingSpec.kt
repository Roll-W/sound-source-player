/*
 * Copyright (C) 2024 RollW
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.rollw.player.data.setting

/**
 * Setting specification.
 *
 * It contains the key and the value entries,
 * and the default value indexes in [valueEntries].
 *
 * @see SettingKey
 * @author RollW
 */
data class SettingSpec<T, V>(
    val key: SettingKey<T, V>,

    private val allowAnyValue: Boolean = false,

    /**
     * The default value indexes in [valueEntries].
     *
     * Only [SettingType.STRING_SET] type can have multiple defaults.
     */
    val defaults: List<Int> = emptyList(),

    /**
     * Leave it empty if the setting has no predefined values.
     */
    val valueEntries: List<V> = emptyList()
) {
    val type = key.type
    val keyName = key.key

    constructor(
        key: SettingKey<T, V>,
        default: Int,
        vararg valueEntries: V
    ) : this(
        key,
        defaults = listOf(default),
        valueEntries = valueEntries.toList()
    )

    constructor(
        key: SettingKey<T, V>,
        default: V
    ) : this(
        key,
        allowAnyValue = true,
        defaults = listOf(0),
        valueEntries = listOf(default)
    )

    constructor(
        key: SettingKey<T, V>,
        default: Int,
        allowAnyValue: Boolean,
        vararg valueEntries: V
    ) : this(key, allowAnyValue, listOf(default), valueEntries.toList())

    init {
        checkDefaults()
    }

    private fun checkDefaults() {
        if (valueEntries.isEmpty() || defaults.isEmpty()) {
            return
        }
        if (defaults.size > 1 && type != SettingType.STRING_SET) {
            throw IllegalArgumentException("Only STRING_SET type can have multiple defaults")
        }
        if (defaults.any { it >= valueEntries.size }) {
            throw IllegalArgumentException("Invalid default index: $defaults for ${valueEntries.size}")
        }
    }

    /**
     * The default value of the setting.
     */
    val defaultValue: T?
        get() {
            if (defaults.isEmpty() || valueEntries.isEmpty()) {
                return null
            }

            @Suppress("UNCHECKED_CAST")
            return if (key.type == SettingType.STRING_SET) {
                defaults.map { valueEntries.elementAtOrNull(it) }.toSet() as T?
            } else {
                defaults.map { valueEntries[it] }.firstOrNull() as T?
            }
        }

    operator fun get(index: Int): V? {
        if (index >= valueEntries.size) {
            return null
        }
        return valueEntries[index]
    }

    fun hasValue(value: V?): Boolean {
        return valueEntries.contains(value)
    }

    fun allowAnyValue() = valueEntries.isEmpty() || allowAnyValue

    @Suppress("UNCHECKED_CAST")
    fun hasValueByType(value: T?): Boolean {
        return if (key.type == SettingType.STRING_SET) {
            valueEntries.containsAll(value as Set<*>)
        } else {
            valueEntries.contains(value as V)
        }
    }

    companion object {
        fun stringSetSpec(
            key: String,
            default: List<Int> = emptyList(),
            vararg valueEntries: String
        ) = SettingSpec(
            SettingKey(key, SettingType.STRING_SET),
            defaults = default,
            valueEntries = valueEntries.toList()
        )

        fun boolean(key: String, default: Boolean = false) = SettingSpec(
            SettingKey(key, SettingType.BOOLEAN),
            default = if (default) 1 else 0,
            valueEntries = arrayOf(true, false)
        )
    }
}
