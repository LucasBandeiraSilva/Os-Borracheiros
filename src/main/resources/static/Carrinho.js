document.addEventListener("DOMContentLoaded", function () {
  let itemCount = sessionStorage.getItem('itemCount') ? parseInt(sessionStorage.getItem('itemCount')) : 0;

  let cartItemCount = document.getElementById("cart-item-count");

  if (cartItemCount) {
    cartItemCount.textContent = itemCount;
  }

  function addItemToCart() {
    itemCount++;
    if (cartItemCount) {
      cartItemCount.textContent = itemCount;
    }
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

  const checkoutButton = document.getElementById("finaliza-pedido");
  if (checkoutButton) {
    checkoutButton.addEventListener("click", function() {
      itemCount = 0;
      if (cartItemCount) {
        cartItemCount.textContent = itemCount;
      }
      sessionStorage.setItem('itemCount', itemCount);
      sessionStorage.removeItem('lastClickedButton');
    });
  }
  const deletar = document.getElementById("deletar");
  if (deletar) {
    deletar.addEventListener("click", function() {
      itemCount = 0;
      if (cartItemCount) {
        cartItemCount.textContent = itemCount;
      }
      sessionStorage.setItem('itemCount', itemCount);
      sessionStorage.removeItem('lastClickedButton');
    });
  }
});
