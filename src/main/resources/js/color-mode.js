/*
 * Rhythm - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Modified version from Symphony, Thanks Symphony :)
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
const isDarkModeInSystem = window.matchMedia("(prefers-color-scheme: dark)").matches;
const mode = localStorage.getItem('color-scheme') || 'auto';
DarkReader.setFetchMethod(window.fetch)
setColorMode(mode);
function toggleColorMode() {
  const body = document.body;
  let mode = localStorage.getItem('color-scheme'); // light / dark / auto
  if (mode === 'dark') {
    mode = isDarkModeInSystem ? 'light' : 'auto';
  } else if (mode === 'light') {
    mode = !isDarkModeInSystem ? 'dark' : 'auto';
  } else {
    mode = isDarkModeInSystem ? 'light' : 'dark';
  }
  setColorMode(mode)
}

function setColorMode(mode) {
    if (mode == 'dark' || (mode == 'auto' && isDarkModeInSystem)) {
        DarkReader.enable({
            brightness: 100,
            contrast: 90,
            sepia: 10
        });
        document.querySelector('#color-mode svg use')?.setAttribute('xlink:href', '#color-moon')
    } else {
        DarkReader.auto(false);
        document.querySelector('#color-mode svg use')?.setAttribute('xlink:href', '#color-sun')
    }
    localStorage.setItem('color-scheme', mode);
}

document.addEventListener("DOMContentLoaded", () => {
    document.querySelector('#color-mode').addEventListener('click', toggleColorMode);
    if (mode == 'dark' || (mode == 'auto' && isDarkModeInSystem)) {
        document.querySelector('#color-mode svg use').setAttribute('xlink:href', '#color-moon');
    } else {
        document.querySelector('#color-mode svg use').setAttribute('xlink:href', '#color-sun');
    }
});