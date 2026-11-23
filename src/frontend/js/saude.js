(() => {
  const STORAGE_KEY = "agroboi_saude";
  const $ = (id) => document.getElementById(id);
  const today = new Date();
  today.setHours(0,0,0,0);
  const dateISO = (date) => {
    const d = new Date(date);
    return new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString().slice(0,10);
  };
  const parseDate = (value) => {
    if (!value) return null;
    const [y,m,d] = value.split("-").map(Number);
    return new Date(y,m-1,d);
  };
  const formatDate = (value) => value ? parseDate(value).toLocaleDateString("pt-BR") : "—";
  const daysUntil = (value) => {
    const date = parseDate(value);
    if (!date) return null;
    return Math.round((date - today) / 86400000);
  };
  const statusOf = (record) => {
    const days = daysUntil(record.nextDate);
    if (days === null) return "Em dia";
    if (days < 0) return "Atrasada";
    if (days <= 30) return "Próxima";
    return "Em dia";
  };
  const escapeHTML = (value) => String(value ?? "").replace(/[&<>"']/g, c => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
  const demo = [
    {id:"h1",animalId:"025",type:"Vacina",productName:"Vacina clostridial",applicationDate:"2026-08-12",nextDate:"2026-12-12",notes:"Registro demonstrativo"},
    {id:"h2",animalId:"026",type:"Medicamento",productName:"Suplementação mineral",applicationDate:"2026-11-02",nextDate:"2026-12-02",notes:"Registro demonstrativo"},
    {id:"h3",animalId:"027",type:"Vacina",productName:"Vacina reprodutiva",applicationDate:"2026-05-10",nextDate:"2026-10-10",notes:"Registro demonstrativo"},
    {id:"h4",animalId:"028",type:"Tratamento",productName:"Acompanhamento veterinário",applicationDate:"2026-11-15",nextDate:"",notes:"Sem próxima data definida"}
  ];
  let records = JSON.parse(localStorage.getItem(STORAGE_KEY) || "null") || demo;
  const save = () => localStorage.setItem(STORAGE_KEY, JSON.stringify(records));

  function render() {
    const query = $("searchHealth").value.trim().toLowerCase();
    const type = $("typeFilter").value;
    const status = $("statusFilter").value;
    const filtered = records.filter(r => {
      const matchesText = [r.animalId,r.type,r.productName].join(" ").toLowerCase().includes(query);
      return matchesText && (!type || r.type === type) && (!status || statusOf(r) === status);
    });
    $("healthTableBody").innerHTML = filtered.map(r => {
      const state = statusOf(r);
      const cls = state === "Atrasada" ? "late" : state === "Próxima" ? "soon" : "ok";
      return `<tr>
        <td class="animal-cell"><strong>#${escapeHTML(r.animalId)}</strong><small>Identificação do animal</small></td>
        <td>${escapeHTML(r.type)}</td><td>${escapeHTML(r.productName)}</td>
        <td>${formatDate(r.applicationDate)}</td><td>${formatDate(r.nextDate)}</td>
        <td><span class="status-pill ${cls}">${state}</span></td>
        <td><button class="table-action" data-edit="${escapeHTML(r.id)}">Editar</button><button class="table-action delete" data-delete="${escapeHTML(r.id)}">Excluir</button></td>
      </tr>`;
    }).join("");
    $("emptyState").hidden = filtered.length !== 0;
    $("totalRecords").textContent = records.length;
    $("upcomingCount").textContent = records.filter(r => statusOf(r) === "Próxima").length;
    $("overdueCount").textContent = records.filter(r => statusOf(r) === "Atrasada").length;
  }

  function openModal(record) {
    $("healthForm").reset();
    $("recordId").value = record?.id || "";
    $("modalTitle").textContent = record ? "Editar registro" : "Novo registro";
    if (record) {
      $("animalId").value = record.animalId;
      $("recordType").value = record.type;
      $("productName").value = record.productName;
      $("applicationDate").value = record.applicationDate;
      $("nextDate").value = record.nextDate || "";
      $("notes").value = record.notes || "";
    } else {
      $("applicationDate").value = dateISO(today);
    }
    $("formFeedback").textContent = "";
    $("healthModal").hidden = false;
    $("animalId").focus();
  }
  function closeModal() { $("healthModal").hidden = true; }

  $("openForm").addEventListener("click", () => openModal());
  $("closeForm").addEventListener("click", closeModal);
  $("cancelForm").addEventListener("click", closeModal);
  $("healthModal").addEventListener("click", e => { if (e.target === $("healthModal")) closeModal(); });
  ["searchHealth","typeFilter","statusFilter"].forEach(id => $(id).addEventListener(id === "searchHealth" ? "input" : "change", render));

  $("healthForm").addEventListener("submit", e => {
    e.preventDefault();
    const applicationDate = $("applicationDate").value;
    const nextDate = $("nextDate").value;
    if (nextDate && nextDate < applicationDate) {
      $("formFeedback").textContent = "A próxima aplicação não pode ser anterior à data do registro.";
      return;
    }
    const id = $("recordId").value || `h${Date.now()}`;
    const record = {
      id, animalId:$("animalId").value.trim().replace(/^#/,""),
      type:$("recordType").value, productName:$("productName").value.trim(),
      applicationDate, nextDate, notes:$("notes").value.trim()
    };
    if (!record.animalId || !record.productName) {
      $("formFeedback").textContent = "Preencha a identificação do animal e o produto.";
      return;
    }
    const index = records.findIndex(r => r.id === id);
    if (index >= 0) records[index] = record; else records.unshift(record);
    save(); render(); closeModal();
  });

  $("healthTableBody").addEventListener("click", e => {
    const editId = e.target.dataset.edit;
    const deleteId = e.target.dataset.delete;
    if (editId) {
      const record = records.find(r => r.id === editId);
      if (record) openModal(record);
    }
    if (deleteId && confirm("Deseja excluir este registro?")) {
      records = records.filter(r => r.id !== deleteId);
      save(); render();
    }
  });

  $("todayDate").textContent = today.toLocaleDateString("pt-BR", {weekday:"long",day:"2-digit",month:"long",year:"numeric"});
  $("menuToggle")?.addEventListener("click", () => $("sidebar")?.classList.toggle("open"));
  save();
  render();
})();
