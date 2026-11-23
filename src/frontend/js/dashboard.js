const todayDate = document.getElementById("todayDate");
const currentYear = document.getElementById("currentYear");
const menuToggle = document.getElementById("menuToggle");
const sidebar = document.getElementById("sidebar");

const now = new Date();
todayDate.textContent = new Intl.DateTimeFormat("pt-BR", {
  day: "2-digit",
  month: "long",
  year: "numeric"
}).format(now);
currentYear.textContent = now.getFullYear();

menuToggle.addEventListener("click", () => {
  const isOpen = sidebar.classList.toggle("open");
  menuToggle.setAttribute("aria-expanded", String(isOpen));
  menuToggle.setAttribute("aria-label", isOpen ? "Fechar menu" : "Abrir menu");
});

// Os dados desta primeira versão são demonstrativos.
// Depois, esta área será substituída por chamadas à API do Backend.
const dashboardData = {
  totalBovinos: 250,
  vacinacoesPendentes: 12,
  pesagensNoMes: 45,
  nascimentosNoMes: 8
};

document.querySelectorAll("[data-count]").forEach((element) => {
  const keyByValue = Object.keys(dashboardData).find(
    (key) => dashboardData[key] === Number(element.dataset.count)
  );

  if (keyByValue) {
    element.textContent = dashboardData[keyByValue].toLocaleString("pt-BR");
  }
});
