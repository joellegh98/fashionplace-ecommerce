// Intercepts submission of any form carrying data-confirm="message" and shows the shared
// Bootstrap confirmation modal instead of the browser's native confirm() dialog. The form
// is only submitted after the user clicks Confirm.
(function () {
    let pendingForm = null;

    const modalEl = document.getElementById('confirmModal');
    if (!modalEl) {
        return;
    }
    const modal = new bootstrap.Modal(modalEl);
    const messageEl = document.getElementById('confirmModalMessage');
    const confirmBtn = document.getElementById('confirmModalConfirm');

    document.addEventListener('submit', function (event) {
        const form = event.target.closest('form[data-confirm]');
        if (!form) {
            return;
        }
        event.preventDefault();
        pendingForm = form;
        messageEl.textContent = form.getAttribute('data-confirm');
        modal.show();
    });

    confirmBtn.addEventListener('click', function () {
        const form = pendingForm;
        pendingForm = null;
        modal.hide();
        if (form) {
            form.submit();
        }
    });

    modalEl.addEventListener('hidden.bs.modal', function () {
        pendingForm = null;
    });
})();
