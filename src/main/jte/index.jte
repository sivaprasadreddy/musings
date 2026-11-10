@import dev.sivalabs.musings.config.ViewHelpers
@import dev.sivalabs.musings.posts.CreatePostRequest
@import dev.sivalabs.musings.posts.PostLikeVM
@import dev.sivalabs.musings.posts.PostVM
@import dev.sivalabs.musings.shared.PagedResult
@import java.util.Map
@import org.springframework.validation.Errors
@param CreatePostRequest postForm
@param Errors postFormErrors = null
@param PagedResult<PostVM> posts
@param Map<Long, PostLikeVM> postLikes
@param String successMessage = null
@param String errorMessage = null
!{var hasErrors = postFormErrors != null && postFormErrors.hasErrors();}
@template.layout(successMessage = successMessage, errorMessage = errorMessage, content = @`
    <div class="mx-auto max-w-5xl space-y-8">
        @if(ViewHelpers.isAuthenticated())
        <section class="rounded-lg bg-white p-6 shadow-sm border border-gray-200" aria-labelledby="new-post-heading">
            <details class="post-composer" open="${hasErrors}">
                <summary class="flex cursor-pointer items-center justify-between rounded-md focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-gray-800">
                    <h1 id="new-post-heading" class="text-xl font-semibold text-gray-900">Post a message</h1>
                    <i class="fas fa-chevron-down post-composer-icon text-gray-600" aria-hidden="true"></i>
                </summary>
                <form method="post" action="/posts" class="mt-4 space-y-4">
                    @template.csrf()
                    <div>
                        <label for="title" class="block text-sm font-medium text-gray-700">Title</label>
                        <input type="text" id="title" name="title" value="${postForm.title()}" required maxlength="200"
                               class="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"/>
                        @template.fieldErrors(errors = postFormErrors, field = "title")
                    </div>
                    <div>
                        <label for="content" class="block text-sm font-medium text-gray-700">Content</label>
                        <textarea id="content" name="content" required maxlength="10000" rows="4"
                                  class="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2">${postForm.content()}</textarea>
                        @template.fieldErrors(errors = postFormErrors, field = "content")
                    </div>
                    <button type="submit" class="rounded-md bg-gray-800 px-5 py-2 text-sm font-medium text-white hover:bg-gray-700">Post message</button>
                </form>
            </details>
        </section>
        @endif
        <section aria-labelledby="posts-heading">
            <h2 id="posts-heading" class="mb-4 text-xl font-semibold text-gray-900">Latest messages</h2>
            @if(posts.data().isEmpty())
            <p class="rounded-lg bg-white p-6 text-gray-500 border border-gray-200">No messages yet. Share the first one!</p>
            @endif
            <div id="post-feed" class="space-y-4">
                @template.posts.feed(posts = posts, postLikes = postLikes)
            </div>
        </section>
    </div>
`)
