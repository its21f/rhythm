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