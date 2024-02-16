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

package tech.rollw.player.audio.tag

import tech.rollw.player.audio.AudioFormatType
import java.io.Closeable

/**
 * @author RollW
 */
interface AudioTag : Closeable {
    val audioFormatType: AudioFormatType

    /**
     * Get the tag field value.
     */
    fun getTagField(field: AudioTagField): String?

    fun getArtwork(): ByteArray?

    fun getAudioProperties(): AudioProperties

    fun getLastModified(): Long

    /**
     * Set the tag field to the given value.
     * Will ignore unsupported fields.
     *
     * If the value is null, the field will be removed.
     */
    fun setTagField(field: AudioTagField, value: String?)

    fun setArtwork(artwork: ByteArray?)

    /**
     * After called [setTagField], the tag will not
     * be saved until [save] is called.
     */
    fun save()

    override fun close()
}