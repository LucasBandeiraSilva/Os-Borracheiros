const form = document.getElementById("form");
const campos = document.querySelectorAll(".required");
const spans = document.querySelectorAll(".span-required");
const emailRegex =
  /^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;

let isValidCpf = false;
let isValidEmail = false;
let isValidName = false;
let isValidpassword = false;

const cepInput = document.getElementById("cep");

form.addEventListener("submit", (event) => {
  nameValidate();
  cpfValidator();
  validateMainPassword();
  comparePassword();

  if (!isValidCpf || !isValidName || !isValidpassword || !isValidEmail) {
    alert("Dados com erro, corrija antes de enviar");
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
function emailRegexValidation() {
  const email = document.getElementById("email").value;

  if (!emailRegex.test(email)) {
    isValidEmail = false;
    setError(1);
  } else {
    isValidEmail = true;
    removeError(1);
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
  console.log("Chamando cpfValidator");

  const cpf = document.getElementById("cpf").value;

  if (!CpfValido(cpf)) {
    setError(2);
    isValidCpf = false;
  } else {
    removeError(2);
    isValidCpf = true;
  }
}

function validateMainPassword() {
  if (campos[3].value.length < 6) {
    setError(3);
  } else {
    removeError(3);
  }
}
function comparePassword() {
  if (campos[4].value === campos[3].value) {
    removeError(4);
    isValidpassword = false;
  } else {
    setError(4);
    isValidpassword = true;
  }
}
