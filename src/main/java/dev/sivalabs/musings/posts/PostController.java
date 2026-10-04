package dev.sivalabs.musings.posts;

import dev.sivalabs.musings.users.AuthUtils;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
class PostController {
    private final PostService postService;

    PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    String home(Model model) {
        model.addAttribute("postForm", new CreatePostRequest("", ""));
        model.addAttribute("posts", postService.findLatest(1));
        return "index";
    }

    @GetMapping("/posts")
    String loadPosts(@RequestParam(defaultValue = "1") int page, Model model) {
        model.addAttribute("posts", postService.findLatest(Math.max(1, page)));
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
            model.addAttribute("posts", postService.findLatest(1));
            return "index";
        }
        postService.createPost(request, authorId);
        redirectAttributes.addFlashAttribute("successMessage", "Your message has been posted");
        return "redirect:/";
    }
}
