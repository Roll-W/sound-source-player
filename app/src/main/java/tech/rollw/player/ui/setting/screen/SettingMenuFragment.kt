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

package tech.rollw.player.ui.setting.screen

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import tech.rollw.player.R
import tech.rollw.player.ui.setting.BasePreferenceFragment

class SettingMenuFragment : BasePreferenceFragment() {
    override fun getTitle() = getString(R.string.setting)

    override fun onCreatePreferencesView(savedInstanceState: Bundle?, rootKey: String?) {
        val navController = findNavController()
    }
}