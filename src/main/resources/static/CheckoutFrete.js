const cepInput = document.getElementById("cep");
const btn = document.getElementById("btn");
const campos = document.querySelectorAll(".required");
const spans = document.querySelectorAll(".span-required");
const freteContainer = document.getElementById("freteContainer"); // Id corrigido

let isValidCep = false;

btn.addEventListener('click', (event) => {
  if (!isValidCep) {
    alert("CEP inválido");
    event.preventDefault();
  }
});

function setError(indice) {
  campos[indice].style.border = "2px solid #9e082d";
  spans[indice].style.display = "block";
}

function removeError(indice) {
  campos[indice].style.border = "2px solid green";
  spans[indice].style.display = "none";
}

async function getCEPInfo(cep) {
  try {
    const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
    const data = await response.json();

    if (data.erro) {
      isValidCep = false;
      setError(0);
      freteContainer.style.display = "none"; // Oculta o campo de frete
      return null;
    }

    isValidCep = true;
    removeError(0);
    freteContainer.style.display = "block"; // Exibe o campo de frete
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
    if (!cepInfo) {
      isValidCep = false;
      setError(0);
      freteContainer.style.display = "none";
    }
  } else {
    isValidCep = false;
    setError(0);
    freteContainer.style.display = "none";
  }
});
