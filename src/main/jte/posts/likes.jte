@import dev.sivalabs.musings.config.ViewHelpers
@import dev.sivalabs.musings.posts.PostLikeVM
@param PostLikeVM likes
!{var action = "/posts/" + likes.postId() + "/" + (likes.liked() ? "unlike" : "like");}
<div id="post-likes-${likes.postId()}" class="post-likes mt-4 flex items-center gap-2" aria-live="polite">
    @if(ViewHelpers.isAuthenticated())
    <form method="post" action="${action}" hx-post="${action}"
          hx-target="closest .post-likes" hx-swap="outerHTML" hx-disable="find button"
          hx-status:4xx="swap:none" hx-status:5xx="swap:none">
        @template.csrf()
        <button type="submit"
                aria-pressed="${likes.liked() ? "true" : "false"}" aria-label="${likes.liked() ? "Unlike" : "Like"}"
                title="${likes.liked() ? "Unlike" : "Like"}"
                class="rounded-md border border-gray-300 px-3 py-1 text-xl hover:bg-gray-100 disabled:opacity-50 ${likes.liked() ? "text-red-600" : "text-gray-600"}">
            <span aria-hidden="true">${likes.liked() ? "♥" : "♡"}</span>
        </button>
    </form>
    @endif
    @if(likes.count() > 0)<span class="like-count">${likes.count()}</span>@endif
    <span class="like-error text-sm text-red-700" role="alert"></span>
</div>
