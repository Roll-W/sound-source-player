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

import tech.rollw.support.io.ImageFormatType

/**
 * Represents the artwork of an audio file.
 *
 * @author RollW
 */
interface Artwork {
    /**
     * The format of the image.
     */
    val format: ImageFormatType

    val data: ByteArray?

    val width: Int

    val height: Int

    val length: Long

    val description: String?

    val type: String?
}