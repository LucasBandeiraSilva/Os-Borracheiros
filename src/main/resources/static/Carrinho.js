document.addEventListener("DOMContentLoaded", function () {
  let itemCount = sessionStorage.getItem('itemCount') ? parseInt(sessionStorage.getItem('itemCount')) : 0;

  document.getElementById("cart-item-count").textContent = itemCount;

  function addItemToCart() {
    itemCount++;
    document.getElementById("cart-item-count").textContent = itemCount;
    sessionStorage.setItem('itemCount', itemCount);
  }

  const buyButtons = document.querySelectorAll("a.btn.btn-success");
  buyButtons.forEach((button, index) => {
    button.addEventListener("click", function() {
      addItemToCart();
      sessionStorage.setItem('lastClickedButton', index); // Armazena o índice do botão clicado
    });
  });
});
