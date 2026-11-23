(() => {
  const STORAGE_KEY = "agroboi_alimentacao";
  const $ = id => document.getElementById(id);
  const today = new Date(); today.setHours(0,0,0,0);
  const dateISO = d => new Date(d.getTime()-d.getTimezoneOffset()*60000).toISOString().slice(0,10);
  const formatDate = value => {
    if (!value) return "—";
    const [y,m,d] = value.split("-").map(Number);
    return new Date(y,m-1,d).toLocaleDateString("pt-BR");
  };
  const money = value => Number(value || 0).toLocaleString("pt-BR",{style:"currency",currency:"BRL"});
  const escapeHTML = value => String(value ?? "").replace(/[&<>"']/g,c=>({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
  const demo = [
    {id:"f1",animalOrGroup:"Lote 01",category:"Ração",feedName:"Ração de engorda",quantity:85,date:"2026-11-20",cost:212.50,notes:"Exemplo demonstrativo"},
    {id:"f2",animalOrGroup:"Lote 02",category:"Pastagem",feedName:"Pasto rotacionado",quantity:120,date:"2026-11-21",cost:0,notes:"Exemplo demonstrativo"},
    {id:"f3",animalOrGroup:"025",category:"Suplemento",feedName:"Suplemento mineral",quantity:2.5,date:"2026-11-22",cost:38.90,notes:"Exemplo demonstrativo"}
  ];
  let entries = JSON.parse(localStorage.getItem(STORAGE_KEY) || "null") || demo;
  const save = () => localStorage.setItem(STORAGE_KEY,JSON.stringify(entries));

  function render(){
    const query=$("searchFeed").value.trim().toLowerCase(), category=$("categoryFilter").value;
    const filtered=entries.filter(e=>[e.animalOrGroup,e.category,e.feedName].join(" ").toLowerCase().includes(query)&&(!category||e.category===category));
    $("feedTableBody").innerHTML=filtered.map(e=>`<tr>
      <td class="feed-animal"><strong>${escapeHTML(e.animalOrGroup)}</strong><small>Animal / lote</small></td>
      <td>${escapeHTML(e.category)}</td><td>${escapeHTML(e.feedName)}</td>
      <td>${Number(e.quantity).toLocaleString("pt-BR")} kg</td><td>${formatDate(e.date)}</td><td>${money(e.cost)}</td>
      <td><button class="feed-action" data-edit="${escapeHTML(e.id)}">Editar</button><button class="feed-action delete" data-delete="${escapeHTML(e.id)}">Excluir</button></td>
    </tr>`).join("");
    $("emptyState").hidden=filtered.length!==0;
    $("totalEntries").textContent=entries.length;
    $("totalQuantity").textContent=entries.reduce((sum,e)=>sum+Number(e.quantity||0),0).toLocaleString("pt-BR",{maximumFractionDigits:2})+" kg";
    $("totalCost").textContent=money(entries.reduce((sum,e)=>sum+Number(e.cost||0),0));
  }
  function openModal(entry){
    $("feedForm").reset(); $("entryId").value=entry?.id||"";
    $("modalTitle").textContent=entry?"Editar registro":"Novo registro";
    if(entry){
      $("animalOrGroup").value=entry.animalOrGroup;$("category").value=entry.category;
      $("feedName").value=entry.feedName;$("quantity").value=entry.quantity;
      $("feedDate").value=entry.date;$("cost").value=entry.cost??"";
      $("feedNotes").value=entry.notes||"";
    }else{$("feedDate").value=dateISO(today);}
    $("formFeedback").textContent="";$("feedModal").hidden=false;$("animalOrGroup").focus();
  }
  function closeModal(){$("feedModal").hidden=true;}
  $("openForm").addEventListener("click",()=>openModal());
  $("closeForm").addEventListener("click",closeModal);
  $("cancelForm").addEventListener("click",closeModal);
  $("feedModal").addEventListener("click",e=>{if(e.target===$("feedModal"))closeModal();});
  $("searchFeed").addEventListener("input",render);$("categoryFilter").addEventListener("change",render);
  $("feedForm").addEventListener("submit",e=>{
    e.preventDefault();
    const quantity=Number($("quantity").value), cost=Number($("cost").value||0);
    if(!(quantity>0)||cost<0){$("formFeedback").textContent="Informe uma quantidade maior que zero e um custo válido.";return;}
    const id=$("entryId").value||`f${Date.now()}`;
    const entry={id,animalOrGroup:$("animalOrGroup").value.trim(),category:$("category").value,feedName:$("feedName").value.trim(),quantity,date:$("feedDate").value,cost,notes:$("feedNotes").value.trim()};
    if(!entry.animalOrGroup||!entry.feedName||!entry.date){$("formFeedback").textContent="Preencha todos os campos obrigatórios.";return;}
    const index=entries.findIndex(item=>item.id===id);
    if(index>=0)entries[index]=entry;else entries.unshift(entry);
    save();render();closeModal();
  });
  $("feedTableBody").addEventListener("click",e=>{
    const edit=e.target.dataset.edit, del=e.target.dataset.delete;
    if(edit){const entry=entries.find(item=>item.id===edit);if(entry)openModal(entry);}
    if(del&&confirm("Deseja excluir este registro?")){entries=entries.filter(item=>item.id!==del);save();render();}
  });
  $("todayDate").textContent=today.toLocaleDateString("pt-BR",{weekday:"long",day:"2-digit",month:"long",year:"numeric"});
  $("menuToggle")?.addEventListener("click",()=>$("sidebar")?.classList.toggle("open"));
  save();render();
})();
