document.getElementById('login-form').addEventListener('submit', event => event.preventDefault());
const loginForm = document.getElementById("loginForm");
const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");
const emailError = document.getElementById("emailError");
const passwordError = document.getElementById("passwordError");
const feedback = document.getElementById("formFeedback");
const togglePassword = document.getElementById("togglePassword");
const forgotPassword = document.getElementById("forgotPassword");

document.getElementById("currentYear").textContent = new Date().getFullYear();

function showFeedback(message, type = "info") {
  feedback.textContent = message;
  feedback.className = `feedback visible ${type}`;
}

function clearErrors() {
  emailError.textContent = "";
  passwordError.textContent = "";
  emailInput.classList.remove("invalid");
  passwordInput.classList.remove("invalid");
  feedback.className = "feedback";
  feedback.textContent = "";
}

function isValidEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

togglePassword.addEventListener("click", () => {
  const isPassword = passwordInput.type === "password";
  passwordInput.type = isPassword ? "text" : "password";
  togglePassword.setAttribute(
    "aria-label",
    isPassword ? "Ocultar senha" : "Mostrar senha"
  );
});

emailInput.addEventListener("input", () => {
  emailError.textContent = "";
  emailInput.classList.remove("invalid");
});

passwordInput.addEventListener("input", () => {
  passwordError.textContent = "";
  passwordInput.classList.remove("invalid");
});

loginForm.addEventListener("submit", (event) => {
  event.preventDefault();
  clearErrors();

  const email = emailInput.value.trim();
  const password = passwordInput.value;

  let hasError = false;

  if (!email) {
    emailError.textContent = "Informe seu e-mail.";
    emailInput.classList.add("invalid");
    hasError = true;
  } else if (!isValidEmail(email)) {
    emailError.textContent = "Informe um e-mail válido.";
    emailInput.classList.add("invalid");
    hasError = true;
  }

  if (!password) {
    passwordError.textContent = "Informe sua senha.";
    passwordInput.classList.add("invalid");
    hasError = true;
  }

  if (hasError) return;

  // A autenticação real será implementada quando o Backend/API estiver pronto.
  showFeedback(
    "Campos validados. A autenticação será concluída após a integração com o Backend.",
    "info"
  );
});

forgotPassword.addEventListener("click", () => {
  showFeedback(
    "A recuperação de senha será disponibilizada em uma próxima etapa do projeto.",
    "info"
  );
});
