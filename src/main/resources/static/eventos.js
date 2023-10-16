const INPUT_BUSCA = document.getElementById('input-busca');
const TABELA = document.getElementById('tabela');

INPUT_BUSCA.addEventListener('keyup', () => {
    let expressao = INPUT_BUSCA.value.toLowerCase();

    let linhas = TABELA.getElementsByTagName('tr');

    console.log(linhas);
    for(let posicao in linhas){
        if(true === isNaN(posicao)){
            continue;
        }
        let conteudoDaLinha = linhas[posicao].innerHTML.toLowerCase();

        if (true === conteudoDaLinha.includes(expressao)){
            linhas[posicao].style.display ='';
        }else{
            linhas[posicao].style.display = 'none';
        }
    }
});

document.addEventListener("DOMContentLoaded", function () {
    const itemList = document.getElementById("iproduto");
    const items = itemList.getElementsByTagName("tr");
    const itemsPerPage = 10;
    const totalPages = Math.ceil(items.length / itemsPerPage);
    let currentPage = 1;

    function showPage(page) {
        for (let i = 0; i < items.length; i++) {
            if (i >= (page - 1) * itemsPerPage && i < page * itemsPerPage) {
                items[i].style.display = "block";
            } else {
                items[i].style.display = "none";
            }
        }
    }

    function updatePaginationButtons() {
        const prevPageButton = document.getElementById("prevPage");
        const nextPageButton = document.getElementById("nextPage");

        prevPageButton.disabled = currentPage === 1;
        nextPageButton.disabled = currentPage === totalPages;
    }

    showPage(currentPage);
    updatePaginationButtons();

    document.getElementById("prevPage").addEventListener("click", function () {
        if (currentPage > 1) {
            currentPage--;
            showPage(currentPage);
            updatePaginationButtons();
        }
    });

    document.getElementById("nextPage").addEventListener("click", function () {
        if (currentPage < totalPages) {
            currentPage++;
            showPage(currentPage);
            updatePaginationButtons();
        }
    });
});
