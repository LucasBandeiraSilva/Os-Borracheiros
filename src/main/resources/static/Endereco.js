const campos = document.querySelectorAll(".required");
const spans = document.querySelectorAll(".span-required");
const cepInput = document.getElementById("cep");
const form = document.getElementById("form");

let isValidCep = false;
let isValidNumber = false;



form.addEventListener("submit",(event) =>{
  houseNumberValidation()

  if (!isValidCep || !isValidNumber) {
    alert("Dados com erro, corrija antes de enviar");
    event.preventDefault();
  }
})

function setError(indice) {
  campos[indice].style.border = "2px solid  #9e082d";
  spans[indice].style.display = "block";
  console.log("Erro definido para o campo", indice);
}

function removeError(indice) {
  campos[indice].style.border = "2px solid green";
  spans[indice].style.display = "none";
  console.log("sem Erro definido para o campo", indice);
}

async function getCEPInfo(cep) {
  try {
    const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
    const data = await response.json();
    if (data.erro) {
      return null; // CEP inválido
    }
    return {
      logradouro: data.logradouro,
      bairro: data.bairro,
      localidade: data.localidade,
      uf: data.uf,
    };
  } catch (error) {
    console.error("Erro ao obter informações do CEP:", error);
    return null;
  }
}

cepInput.addEventListener("input", async () => {
  const cep = cepInput.value.replace(/\D/g, "");
  if (cep.length === 8) {
    const cepInfo = await getCEPInfo(cep);
    if (cepInfo) {
      document.getElementById("logradouro").value = cepInfo.logradouro;
      document.getElementById("bairro").value = cepInfo.bairro;
      document.getElementById("cidade").value = cepInfo.localidade;
      document.getElementById("estado").value = cepInfo.uf;
      document.getElementById("endereco").value = cepInfo.logradouro;
      document.getElementById("enderecoFaturamento").value = cepInfo.logradouro;

      removeError(0);
      isValidCep = true;
    }
  } else {
    setError(0);
    isValidCep = false;
  }
});
function houseNumberValidation() {
  const numero = campos[1].value;

  if (numero === "" || numero === null || numero < 1) {
    isValidNumber = false;
    setError(1);
  } else {
    isValidNumber = true;
    removeError(1);
  }
}
