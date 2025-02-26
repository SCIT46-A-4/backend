function togglePasswordVisibility(passwordId, checkboxId) {
  const passwordInput = document.getElementById(passwordId);
  const checkbox = document.getElementById(checkboxId);
  if (checkbox.checked) {
    passwordInput.type = 'text';
  } else {
    passwordInput.type = 'password';
  }
}
