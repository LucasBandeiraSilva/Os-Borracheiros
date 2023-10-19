const form = document.getElementById("form");
const campos = document.querySelectorAll(".required");
const spans = document.querySelectorAll(".span-required");
const emailRegex =
  /^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
let isValidCpf = false;
let isValidCep = false;

const cepInput = document.getElementById("cep");



form.addEventListener("submit", (event) => {
  nameValidate();
  dateValidation()
  emailRegexValidation();
  cpfValidator();
  validateMainPassword();
  comparePassword();

  if (!isValidCpf) {
    alert("ze da manga");
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
  } else {
    removeError(0);
  }
}
function dateValidation(){
  const date = document.getElementById("dataAniversario").value

  if(date === '' || date === null){
    setError(1)
  }else{
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
function CpfValido(cpf) {
  if (cpf == "00000000000") return false;
  if (cpf.length != 11) return false;

  var soma = 0;
  var resto;

  for (i = 1; i <= 9; i++) {
    soma = soma + parseInt(cpf.substring(i - 1, i)) * (11 - i);
  }

  resto = (soma * 10) % 11;
  if (resto == 10 || resto == 11) resto = 0;

  if (resto != parseInt(cpf.substring(9, 10))) return false;

  soma = 0;
  for (i = 1; i <= 10; i++) {
    soma = soma + parseInt(cpf.substring(i - 1, i)) * (12 - i);
  }

  resto = (soma * 10) % 11;
  if (resto == 10 || resto == 11) resto = 0;

  if (resto != parseInt(cpf.substring(10, 11))) return false;
  return true;
}

function cpfValidator() {
  const cpf = document.getElementById("cpf").value;

  if (!CpfValido(cpf)) {
    setError(3);
    isValidCpf = false;
  } else {
    removeError(3);
    isValidCpf = true;
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
    removeError(5);
  } else {
    setError(5);
  }
}async function getCEPInfo(cep) {
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
    console.error('Erro ao obter informações do CEP:', error);
    return null;
  }
}

cepInput.addEventListener('input', async () => {
  const cep = cepInput.value.replace(/\D/g, '');
  if (cep.length === 8) {
    const cepInfo = await getCEPInfo(cep);
    if (cepInfo) {
      document.getElementById('logradouro').value = cepInfo.logradouro;
      document.getElementById('bairro').value = cepInfo.bairro;
      document.getElementById('cidade').value = cepInfo.localidade;
      document.getElementById('estado').value = cepInfo.uf;
      document.getElementById('endereco').value = cepInfo.logradouro
      document.getElementById('enderecoFaturamento').value = cepInfo.logradouro

      removeError(6);
      isValidCep = false;
    } 
  }
  else {
    setError(6);
    isValidCep = true;
  }
});