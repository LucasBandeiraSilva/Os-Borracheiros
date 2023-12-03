const enderecoPadrao = document.querySelectorAll(".padrao");

if (enderecoPadrao.length > 0) {
    enderecoPadrao.forEach(function(elemento) {
        elemento.addEventListener("click", function() {
            alert("O endereço foi definido como padrão");
        });
    });
}
