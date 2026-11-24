const STORAGE_KEY = "agroboi_animais_v2";
const initialAnimals = [];
let animals = loadAnimals();

const formPanel = document.getElementById("animalFormPanel");
const form = document.getElementById("animalForm");
const tableBody = document.getElementById("animalsTable");
const searchInput = document.getElementById("searchInput");
const statusFilter = document.getElementById("statusFilter");
const feedback = document.getElementById("formFeedback");

document.getElementById("currentYear").textContent = new Date().getFullYear();
document.getElementById("todayDate").textContent = new Intl.DateTimeFormat("pt-BR",{day:"2-digit",month:"long",year:"numeric"}).format(new Date());

function loadAnimals(){
  try {
    const saved = localStorage.getItem(STORAGE_KEY);
    const parsed = saved ? JSON.parse(saved) : initialAnimals;
    return Array.isArray(parsed) ? parsed : initialAnimals;
  } catch {
    return initialAnimals;
  }
}
function saveAnimals(){ localStorage.setItem(STORAGE_KEY, JSON.stringify(animals)); }
function escapeHTML(value){
  return String(value ?? "").replace(/[&<>"']/g, char => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"}[char]));
}
function formatDate(value){
  if(!value) return "—";
  const [year,month,day] = value.split("-");
  return `${day}/${month}/${year}`;
}
function statusClass(status){
  if(status === "NA_FAZENDA") return "active";
  if(status === "VENDIDO") return "sold";
  return "inactive";
}
function updateSummary(){
  document.getElementById("totalAnimals").textContent = animals.length;
  document.getElementById("activeAnimals").textContent = animals.filter(a=>a.situacao==="NA_FAZENDA").length;
  document.getElementById("monitorAnimals").textContent = animals.filter(a=>a.situacao==="VENDIDO").length;
}
function renderAnimals(){
  const term = searchInput.value.trim().toLocaleLowerCase("pt-BR");
  const selectedStatus = statusFilter.value;
  const filtered = animals.filter(animal => {
    const matchesText = [animal.identificacao,animal.nome,animal.racaId].join(" ").toLocaleLowerCase("pt-BR").includes(term);
    return matchesText && (!selectedStatus || animal.situacao === selectedStatus);
  });
  tableBody.innerHTML = filtered.map(animal => `
    <tr>
      <td><strong>#${escapeHTML(animal.identificacao)}</strong></td>
      <td>${escapeHTML(animal.nome || "—")}</td>
      <td>${escapeHTML(animal.racaId ?? "—")}</td>
      <td>${escapeHTML(AnimalContract.labels[animal.sexo])}</td>
      <td>${formatDate(animal.dataNascimento)}</td>
      <td><span class="status ${statusClass(animal.situacao)}">${escapeHTML(AnimalContract.labels[animal.situacao])}</span></td>
      <td><div class="action-group"><button class="action-button" data-action="edit" data-id="${escapeHTML(animal.id)}">Editar</button><button class="action-button delete" data-action="delete" data-id="${escapeHTML(animal.id)}">Excluir</button></div></td>
    </tr>`).join("");
  document.getElementById("emptyState").hidden = filtered.length !== 0;
  updateSummary();
}
function openForm(animal=null){
  form.reset();
  document.getElementById("animalId").value = animal?.id || "";
  document.getElementById("formTitle").textContent = animal ? "Editar bovino" : "Cadastrar novo bovino";
  document.getElementById("saveButton").textContent = animal ? "Salvar alterações" : "Salvar animal";
  if(animal){
    for(const key of ["identificacao","nome","racaId","sexo","dataNascimento","observacoes","situacao","maeId","paiId"]){
      document.getElementById(key).value = animal[key] || "";
    }
  } else {
    document.getElementById("situacao").value = "NA_FAZENDA";
  }
  feedback.textContent = "";
  formPanel.classList.add("open");
  formPanel.scrollIntoView({behavior:"smooth",block:"start"});
  document.getElementById("identificacao").focus({preventScroll:true});
}
function closeForm(){formPanel.classList.remove("open");form.reset();feedback.textContent="";}
document.getElementById("newAnimalButton").addEventListener("click",()=>openForm());
document.getElementById("closeFormButton").addEventListener("click",closeForm);
document.getElementById("cancelButton").addEventListener("click",closeForm);
searchInput.addEventListener("input",renderAnimals);
statusFilter.addEventListener("change",renderAnimals);

form.addEventListener("submit",event=>{
  event.preventDefault();
  feedback.textContent = "";
  let data;
  try { data = AnimalContract.request(Object.fromEntries(new FormData(form).entries())); }
  catch (error) { feedback.textContent = error.message; return; }
  const editingId = Number(document.getElementById("animalId").value) || null;
  const duplicate = animals.some(a=>a.identificacao.toLowerCase()===data.identificacao.toLowerCase() && a.id!==editingId);
  if(duplicate){
    feedback.textContent = "Já existe um animal com esse número de identificação.";
    document.getElementById("identificacao").focus();
    return;
  }
  if(editingId){
    animals = animals.map(a=>a.id===editingId ? {...a,...data,id:editingId} : a);
  } else {
    animals.unshift({...data,id:Math.max(0, ...animals.map(a => a.id)) + 1});
  }
  saveAnimals();
  renderAnimals();
  closeForm();
});

tableBody.addEventListener("click",event=>{
  const button = event.target.closest("button[data-action]");
  if(!button) return;
  const animal = animals.find(a=>a.id===Number(button.dataset.id));
  if(!animal) return;
  if(button.dataset.action==="edit") openForm(animal);
  if(button.dataset.action==="delete"){
    if(!confirm(`Deseja realmente excluir o animal #${animal.identificacao}?`)) return;
    animals = animals.filter(a=>a.id!==animal.id);
    saveAnimals();
    renderAnimals();
  }
});

const menuToggle=document.getElementById("menuToggle");
const sidebar=document.getElementById("sidebar");
menuToggle.addEventListener("click",()=>{
  const open=sidebar.classList.toggle("open");
  menuToggle.setAttribute("aria-expanded",String(open));
  menuToggle.setAttribute("aria-label",open?"Fechar menu":"Abrir menu");
});

renderAnimals();

if (localStorage.getItem("agroboi_animais")) document.getElementById("legacyNotice").textContent = "Existem registros da versão anterior preservados no navegador. Revise-os antes de recadastrar com os campos atuais.";
