document.addEventListener("DOMContentLoaded", function () {
    let itemCount = sessionStorage.getItem('itemCount') ? parseInt(sessionStorage.getItem('itemCount')) : 0;
  
    function addItemToCart() {
      itemCount++;
      document.getElementById("cart-item-count").textContent = itemCount;
      sessionStorage.setItem('itemCount', itemCount);
    }
  
    const buyButtons = document.querySelectorAll("a.btn.btn-success");
    buyButtons.forEach((button, index) => {
      button.addEventListener("click", function() {
        addItemToCart();
        sessionStorage.setItem('lastClickedButton', index);
        alert("Adicionado ao carrinho.");
      });
    });
  
    // botão de conclusão da compra 
    const checkoutButton = document.getElementById("finaliza-pedido");
    // evento de clique ao botão de conclusão da compra
    checkoutButton.addEventListener("click", function() {
      console.log("Clique no botão de conclusão da compra.");
      itemCount = 0;
      document.getElementById("cart-item-count").textContent = itemCount;
      sessionStorage.setItem('itemCount', itemCount);
      sessionStorage.removeItem('lastClickedButton');
      alert("Compra concluída, carrinho zerado.");
    });
  
    // Atualize o contador no carregamento inicial
    document.getElementById("cart-item-count").textContent = itemCount;
  });
  