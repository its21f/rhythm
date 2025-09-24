const isDarkModeInSystem = window.matchMedia("(prefers-color-scheme: dark)").matches;
const darkMode = localstorage.getItem('color-scheme') || 'auto';
function toggleDarkMode() {
  const body = document.body;
  let darkMode = localStorage.getItem('color-scheme'); // light / dark / auto
  if (darkMode === 'dark') {
    darkMode = isDarkModeInSystem ? 'light' : 'auto';
  } else if (darkMode === 'light') {
    darkMode = !isDarkModeInSystem ? 'dark' : 'auto';
  } else {
      darkMode = isDarkModeInSystem ? 'light' : 'dark';
  }
  localStorage.setItem('color-scheme', darkMode);
  if (darkMode == 'dark' || (darkMode == 'auto' && isDarkModeInSystem)) {
    DarkReader.auto({
        brightness: 100,
        contrast: 90,
        sepia: 10
    });
  } else {
    DarkReader.auto(false);
  }
}
