// Toggles a product on the wishlist without reloading the page.
// Any element with data-wishlist-add="<productId>" acts as a Save/Saved toggle button.
(function () {
    document.addEventListener('click', function (event) {
        const button = event.target.closest('[data-wishlist-add]');
        if (!button || button.disabled) {
            return;
        }
        event.preventDefault();

        const productId = button.getAttribute('data-wishlist-add');
        button.disabled = true;
        fetch('/wishlist/toggle', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'productId=' + encodeURIComponent(productId)
        }).then(function (response) {
            if (!response.ok) {
                throw new Error('Toggle failed');
            }
            return response.json();
        }).then(function (data) {
            setButtonState(button, data.saved);
            button.disabled = false;
        }).catch(function () {
            button.disabled = false;
            button.textContent = 'Try again';
        });
    });

    function setButtonState(button, saved) {
        const labelUnsaved = button.getAttribute('data-label-unsaved') || 'Save';
        button.classList.toggle('btn-success', saved);
        button.classList.toggle('btn-outline-secondary', !saved);
        button.innerHTML = saved ? '\u2665 Saved' : '\u2661 ' + labelUnsaved;

        // Reveal/hide an optional "View wishlist" link sitting next to the button.
        const extra = button.parentElement
            ? button.parentElement.querySelector('[data-wishlist-saved-extra]')
            : null;
        if (extra) {
            extra.classList.toggle('d-none', !saved);
        }
    }
})();
