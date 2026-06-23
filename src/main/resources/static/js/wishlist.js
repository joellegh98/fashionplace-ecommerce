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

        if (!isAuthenticated()) {
            showLoginModal(productId);
            return;
        }

        button.disabled = true;

        const csrfToken = document.querySelector('meta[name="_csrf"]');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]');
        const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
        if (csrfToken && csrfHeader) {
            headers[csrfHeader.content] = csrfToken.content;
        }

        fetch('/wishlist/toggle', {
            method: 'POST',
            headers: headers,
            body: 'productId=' + encodeURIComponent(productId)
        }).then(function (response) {
            if (response.status === 401 || response.status === 403) {
                showLoginModal(productId);
                throw new Error('auth');
            }
            if (!response.ok) {
                throw new Error('toggle-failed');
            }
            return response.json();
        }).then(function (data) {
            setButtonState(button, data.saved);
            button.disabled = false;
        }).catch(function (error) {
            button.disabled = false;
            if (error && error.message === 'auth') {
                return;
            }
            button.textContent = 'Try again';
        });
    });

    function isAuthenticated() {
        const authMeta = document.querySelector('meta[name="user-authenticated"]');
        return authMeta && authMeta.content === 'true';
    }

    function showLoginModal(productId) {
        const modalEl = document.getElementById('wishlistLoginModal');
        if (!modalEl || typeof bootstrap === 'undefined') {
            return;
        }
        const loginLink = modalEl.querySelector('[data-wishlist-login]');
        if (loginLink) {
            loginLink.href = productId
                ? '/login?wishlistProductId=' + encodeURIComponent(productId)
                : '/login';
        }
        bootstrap.Modal.getOrCreateInstance(modalEl).show();
    }

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
