package dev.sivalabs.musings.posts;

import dev.sivalabs.musings.users.AuthUtils;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
class PostController {
    private final PostService postService;

    private final PostLikeService postLikeService;

    PostController(PostService postService, PostLikeService postLikeService) {
        this.postService = postService;
        this.postLikeService = postLikeService;
    }

    @GetMapping("/")
    String home(Model model) {
        model.addAttribute("postForm", new CreatePostRequest("", ""));
        addFeed(model, 1);
        return "index";
    }

    @GetMapping("/posts")
    String loadPosts(@RequestParam(defaultValue = "1") int page, Model model) {
        addFeed(model, Math.max(1, page));
        return "posts/feed :: feed";
    }

    @PostMapping("/posts")
    String createPost(
            @Valid @ModelAttribute("postForm") CreatePostRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        Long authorId = AuthUtils.getCurrentUserIdOrThrow();
        if (bindingResult.hasErrors()) {
            addFeed(model, 1);
            return "index";
        }
        postService.createPost(request, authorId);
        redirectAttributes.addFlashAttribute("successMessage", "Your message has been posted");
        return "redirect:/";
    }

    @PostMapping("/posts/{postId}/like")
    String like(@PathVariable Long postId, Model model) {
        model.addAttribute("likes", postLikeService.setLiked(postId, AuthUtils.getCurrentUserIdOrThrow(), true));
        return "posts/likes :: likes";
    }

    @PostMapping("/posts/{postId}/unlike")
    String unlike(@PathVariable Long postId, Model model) {
        model.addAttribute("likes", postLikeService.setLiked(postId, AuthUtils.getCurrentUserIdOrThrow(), false));
        return "posts/likes :: likes";
    }

    private void addFeed(Model model, int page) {
        var posts = postService.findLatest(page);
        model.addAttribute("posts", posts);
        model.addAttribute(
                "postLikes",
                postLikeService.findForPosts(
                        posts.data().stream().map(PostVM::id).toList(), AuthUtils.getCurrentUserId()));
    }
}
