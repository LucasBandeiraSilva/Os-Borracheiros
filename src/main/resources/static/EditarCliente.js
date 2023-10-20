const form = document.getElementById("form");
const campos = document.querySelectorAll(".required");
const spans = document.querySelectorAll(".span-required");
const emailRegex =
  /^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
let isValidCpf = false;
let isValidCep = false;
let isValidName = false;
let isValidDate = false;
let isValidNumber = false;
let isValidPassword = false;

const cepInput = document.getElementById("cep");

form.addEventListener("submit", (event) => {
  nameValidate();
  dateValidation();
  validateMainPassword();
  comparePassword();
  houseNumberValidation()

  if (!isValidName || !isValidDate || !isValidCep || !isValidNumber || !isValidPassword) {
    alert("erros no formulario, corrija");
    event.preventDefault();
  }
});

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

function nameValidate() {
  const nome = document.getElementById("nome").value;
  if (nome.length < 3) {
    setError(0);
    isValidName = false;
  } else {
    removeError(0);
    isValidName = true;
  }
}
function dateValidation() {
  const date = document.getElementById("dataAniversario").value;

  if (date === "" || date === null) {
    isValidDate = false;
    setError(1);
  } else {
    isValidDate = true;
    removeError(1);
  }
}
function emailRegexValidation() {
  const email = document.getElementById("email").value;

  if (!emailRegex.test(email)) {
    setError(2);
  } else {
    removeError(2);
  }
}

function validateMainPassword() {
  if (campos[4].value.length < 6) {
    setError(4);
  } else {
    removeError(4);
  }
}
function comparePassword() {
  if (campos[4].value === campos[5].value) {
    isValidPassword = true;
    removeError(5);
  } else {
    isValidPassword = false;
    setError(5);
  }
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

         isValidCep = true;
      removeError(6);
    }
  } else {
      isValidCep = false;
    setError(6);
  }
});

function houseNumberValidation() {
  const numero = campos[7].value; // Corrigido para campos[6]

  if (numero === '' || numero === null || numero < 1) {
    isValidNumber = false;
    setError(7); // Corrigido para 6
  } else {
    isValidNumber = true;
    removeError(7); // Corrigido para 6
  }
}
