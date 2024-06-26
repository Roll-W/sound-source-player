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

#ifndef SOUNDSOURCE_IMAGE_H
#define SOUNDSOURCE_IMAGE_H

#include <sys/types.h>
#include <memory>
#include <assert.h>

#include <imageinfo.hpp>

namespace SoundSource::Image {
    class ImageProcessor {
    public:
        static int32_t *BlurArgb8888(int32_t *pix, int32_t w, int32_t h, int32_t radius);

        static int16_t *BlurRgb565(int16_t *pix, int32_t w, int32_t h, int32_t radius);
    };

    ImageInfo getImageInfo(const char *path);

    ImageInfo getImageInfo(const void *data, size_t size);
}
#endif //SOUNDSOURCE_IMAGE_H
