@import dev.sivalabs.musings.config.ViewHelpers
@import dev.sivalabs.musings.posts.PostLikeVM
@import dev.sivalabs.musings.posts.PostVM
@import dev.sivalabs.musings.shared.PagedResult
@import java.util.Map
@param PagedResult<PostVM> posts
@param Map<Long, PostLikeVM> postLikes
@for(var post : posts.data())
    <article class="rounded-lg bg-white p-6 shadow-sm border border-gray-200">
        <h3 class="text-xl font-semibold text-gray-900 break-words">${post.title()}</h3>
        <div class="mt-2 text-sm text-gray-500">
            <span>${post.authorName()}</span>
            <span aria-hidden="true"> &middot; </span>
            <time datetime="${String.valueOf(post.createdAt())}">${ViewHelpers.formatDateTime(post.createdAt())}</time>
        </div>
        <p class="mt-4 text-gray-700 whitespace-pre-wrap break-words">${post.content()}</p>
        @template.posts.likes(likes = postLikes.get(post.id()))
    </article>
@endfor
@if(posts.hasNextPage())
    <div class="text-center">
        <button type="button" hx-get="/posts?page=${posts.currentPageNo() + 1}"
                hx-target="closest div" hx-swap="outerHTML"
                class="rounded-md bg-gray-800 px-5 py-2 text-sm font-medium text-white hover:bg-gray-700">
            Load more
        </button>
    </div>
@endif
