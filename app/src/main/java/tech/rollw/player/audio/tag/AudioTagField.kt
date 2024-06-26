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

/**
 * @author RollW
 */
enum class AudioTagField {
    TITLE,
    ALBUM,
    ALBUM_ARTIST("ALBUMARTIST"),
    ARTIST,
    ARRANGER,
    BPM,
    COMPOSER,
    CONDUCTOR,
    LYRICIST,
    GENRE,
    DATE,
    TRACK_NUMBER("TRACKNUMBER"),
    DISC_NUMBER("DISCNUMBER"),
    COMMENT,
    LYRICS,
    COPYRIGHT,
    LABEL,
    LANGUAGE,
    ;

    val value: String

    constructor(value: String) {
        this.value = value
    }

    constructor() {
        this.value = name
    }

}