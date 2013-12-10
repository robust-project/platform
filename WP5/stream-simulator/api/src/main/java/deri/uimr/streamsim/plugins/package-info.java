/**
 * Copyright 2013 DERI, National University of Ireland Galway.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Stream Simulator plugins API package.
 *
 * <p>This package contains the plugin classes needed to extend the stream simulator.</p>
 *
 * <p><b>Note about the Simulator WebAdmin:</b></p>
 *
 * <p>The WebAdmin tool uses getter/setter method naming conventions to automatically discover data which the plugins may expose to the status/management sections. All getters will be used to fetch status information and current variable settings, and all setters will be used for manipulating internal variables.</p>
 *
 * @author Hugo Hromic
 * @since 5.0
 */

package deri.uimr.streamsim.plugins;
