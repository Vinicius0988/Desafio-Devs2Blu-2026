const STORAGE_KEY = "agroboi_animais";
const initialAnimals = [
  {id:"1",identification:"025",name:"Mimosa",breed:"Holandesa",sex:"Fêmea",birthDate:"2022-03-14",weight:"480",origin:"Nascida na propriedade",status:"Ativo"},
  {id:"2",identification:"026",name:"Estrela",breed:"Jersey",sex:"Fêmea",birthDate:"2021-11-02",weight:"390",origin:"Compra",status:"Ativo"},
  {id:"3",identification:"027",name:"Trovão",breed:"Nelore",sex:"Macho",birthDate:"2023-01-20",weight:"520",origin:"Nascido na propriedade",status:"Ativo"},
  {id:"4",identification:"028",name:"Flor",breed:"Angus",sex:"Fêmea",birthDate:"2022-08-09",weight:"410",origin:"Compra",status:"Em acompanhamento"}
];
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
    return saved ? JSON.parse(saved) : initialAnimals;
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
  if(status === "Ativo") return "active";
  if(status === "Em acompanhamento") return "monitor";
  if(status === "Vendido") return "sold";
  return "inactive";
}
function updateSummary(){
  document.getElementById("totalAnimals").textContent = animals.length;
  document.getElementById("activeAnimals").textContent = animals.filter(a=>a.status==="Ativo").length;
  document.getElementById("monitorAnimals").textContent = animals.filter(a=>a.status==="Em acompanhamento").length;
}
function renderAnimals(){
  const term = searchInput.value.trim().toLocaleLowerCase("pt-BR");
  const selectedStatus = statusFilter.value;
  const filtered = animals.filter(animal => {
    const matchesText = [animal.identification,animal.name,animal.breed].join(" ").toLocaleLowerCase("pt-BR").includes(term);
    return matchesText && (!selectedStatus || animal.status === selectedStatus);
  });
  tableBody.innerHTML = filtered.map(animal => `
    <tr>
      <td><strong>#${escapeHTML(animal.identification)}</strong></td>
      <td>${escapeHTML(animal.name || "—")}</td>
      <td>${escapeHTML(animal.breed)}</td>
      <td>${escapeHTML(animal.sex)}</td>
      <td>${formatDate(animal.birthDate)}</td>
      <td>${animal.weight ? `${escapeHTML(animal.weight)} kg` : "—"}</td>
      <td><span class="status ${statusClass(animal.status)}">${escapeHTML(animal.status)}</span></td>
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
    for(const key of ["identification","name","breed","sex","birthDate","weight","origin","status"]){
      document.getElementById(key).value = animal[key] || "";
    }
  } else {
    document.getElementById("status").value = "Ativo";
  }
  feedback.textContent = "";
  formPanel.classList.add("open");
  formPanel.scrollIntoView({behavior:"smooth",block:"start"});
  document.getElementById("identification").focus({preventScroll:true});
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
  const data = Object.fromEntries(new FormData(form).entries());
  data.identification = data.identification.trim();
  data.name = data.name.trim();
  if(!data.identification || !data.breed || !data.sex || !data.status){
    feedback.textContent = "Preencha os campos obrigatórios.";
    return;
  }
  const editingId = document.getElementById("animalId").value;
  const duplicate = animals.some(a=>a.identification.toLowerCase()===data.identification.toLowerCase() && a.id!==editingId);
  if(duplicate){
    feedback.textContent = "Já existe um animal com esse número de identificação.";
    document.getElementById("identification").focus();
    return;
  }
  if(editingId){
    animals = animals.map(a=>a.id===editingId ? {...a,...data,id:editingId} : a);
  } else {
    animals.unshift({...data,id:crypto.randomUUID ? crypto.randomUUID() : String(Date.now())});
  }
  saveAnimals();
  renderAnimals();
  closeForm();
});

tableBody.addEventListener("click",event=>{
  const button = event.target.closest("button[data-action]");
  if(!button) return;
  const animal = animals.find(a=>a.id===button.dataset.id);
  if(!animal) return;
  if(button.dataset.action==="edit") openForm(animal);
  if(button.dataset.action==="delete"){
    if(!confirm(`Deseja realmente excluir o animal #${animal.identification}?`)) return;
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
