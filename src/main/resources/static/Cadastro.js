const form = document.getElementById("form");
const campos = document.querySelectorAll(".required");
const spans = document.querySelectorAll(".span-required");
const emailRegex = /^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;

form.addEventListener("submit", (event) => {
  nameValidate();
  emailRegexValidation();
  cpfNullValidator()
  validateMainPassword();
  comparePassword();

  const hasErrors = document.querySelectorAll(".span-required").length > 0;

  if (hasErrors) {
    event.preventDefault();
    alert("errrro") // Somente previne a submissão se houver erros
  }
});

function setError(indice) {
  campos[indice].style.border = "2px solid  #9e082d";
  spans[indice].style.display = "block";
  console.log('Erro definido para o campo', indice);

}

function removeError(indice) {
  campos[indice].style.border = "2px solid green";
  spans[indice].style.display = "none";
}

function nameValidate() {
  if (campos[0].value.length < 3) {
    setError(0);
  } else {
    removeError(0);
  }
}

function emailRegexValidation() {
  if (emailRegex.test(campos[2].value)) {
    removeError(2);
  } else {
    setError(2);
  }
}
function cpfNullValidator(){
  if(campos[3].value.trim() == ''){
    removeError(3)
  }else{
    setError(3)
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
  if (campos[4].value === campos[5].value && campos[5].value.length > 5) {
    removeError(5);
  } else {
    setError(5);
  }
}
