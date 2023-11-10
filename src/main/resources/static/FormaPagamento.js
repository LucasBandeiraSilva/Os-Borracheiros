function showPaymentForm() {
    var tipoPagamento = document.getElementById("tipoPagamento").value;

    // Oculta todos os formulários
    document.getElementById("boletoForm").style.display = "none";
    document.getElementById("cartaoForm").style.display = "none";

    // Mostra o formulário correspondente ao tipo de pagamento selecionado
    if (tipoPagamento === "boleto") {
        document.getElementById("boletoForm").style.display = "block";
    } else if (tipoPagamento === "cartao") {
        document.getElementById("cartaoForm").style.display = "block";
    }
}

function gerarNumeroBoleto() {
    var numeroBoleto = "";
    for (var i = 0; i < 48; i++) {
        numeroBoleto += Math.floor(Math.random() * 10);
    }
    document.getElementById("campoBoleto").value = numeroBoleto;
}