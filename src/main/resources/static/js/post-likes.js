(() => {
    function showLikeError(event) {
        const source = event.detail.ctx?.sourceElement || event.target;
        const controls = source.closest?.('.post-likes');
        if (!controls) return;
        const message = event.detail.ctx?.response?.status === 404
            ? 'This post is no longer available'
            : 'Could not update your like. Please try again.';
        controls.querySelector('.like-error').textContent = message;
    }
    document.addEventListener('htmx:response:error', showLikeError);
    document.addEventListener('htmx:error', showLikeError);
})();
