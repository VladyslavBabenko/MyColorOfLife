package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.entity.Article;
import com.github.vladyslavbabenko.mycoloroflife.service.ArticleService;
import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/article")
public class ArticleController {
    private final ArticleService articleService;
    private final UserService userService;

    @GetMapping(path = {"", "/page/{pageId}"})
    public String getArticles(Model model, String keyword, @PathVariable(value = "pageId", required = false) Integer pageId) {
        if (pageId == null) {
            pageId = 1;
        }

        List<Article> articles;

        if (keyword != null) {
            articles = articleService.findByKeyword(keyword);
        } else {
            articles = articleService.getAllArticles();
        }

        List<Article> listOfArticles = articles.stream().skip(pageId * 6L - 6).limit(6).collect(Collectors.toList());

        int[] numberOfPages = new int[(int) Math.ceil(articles.size() / 6.0)];

        for (int i = 0; i < numberOfPages.length; i++) {
            numberOfPages[i] = i;
        }

        model.addAttribute("listOfArticles", listOfArticles);
        model.addAttribute("pageID", pageId);
        model.addAttribute("numberOfPages", numberOfPages);

        return "generalTemplate/articlesPage";
    }

    @GetMapping("/{articleId}")
    public String getArticleById(@PathVariable("articleId") Integer articleId, Model model) {
        model.addAttribute("article", articleService.findById(articleId));
        return "generalTemplate/articlePage";
    }

    @GetMapping("/new")
    public String newArticle(Model model) {
        model.addAttribute("article", new Article());
        return "authorTemplate/newArticlePage";
    }

    @PostMapping()
    public String createArticle(@ModelAttribute @Valid Article article, BindingResult bindingResult, Model model) {
        article.setUsers(Collections.singleton(userService.getCurrentUser()));

        if (bindingResult.hasErrors()) {
            return "authorTemplate/newArticlePage";
        }

        if (!articleService.saveArticle(article)) {
            model.addAttribute("articleError", "Така стаття вже існує");
            return "authorTemplate/newArticlePage";
        }

        return "redirect:/article";
    }

    @DeleteMapping("/{articleId}")
    public String deleteArticle(@PathVariable("articleId") Integer articleId) {
        if (userService.getCurrentUser().getRoles().stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase("ROLE_ADMIN") ||
                        role.getRoleName().equalsIgnoreCase("ROLE_AUTHOR"))) {
            articleService.deleteArticle(articleId);
        }
        return "redirect:/article";
    }

    @GetMapping("/{articleId}/edit")
    public String toArticleEdit(@PathVariable("articleId") Integer articleId, Model model) {
        Article article = articleService.findById(articleId);

        boolean isAdmin = userService.getCurrentUser().getRoles().stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase("ROLE_ADMIN"));

        if (article.getUsers().contains(userService.getCurrentUser()) || isAdmin) {
            model.addAttribute("article", article);
            return "authorTemplate/editArticlePage";
        } else {
            return "error/accessDeniedPage";
        }
    }

    @PutMapping()
    public String updateArticle(@ModelAttribute @Valid Article article, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "authorTemplate/editArticlePage";
        }

        if (!articleService.updateArticle(article)) {
            model.addAttribute("updateArticleError", "Такої статті не існує");
            return "authorTemplate/editArticlePage";
        }

        return "redirect:/article";
    }
}