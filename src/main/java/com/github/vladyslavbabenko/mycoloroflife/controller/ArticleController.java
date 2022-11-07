package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.entity.Article;
import com.github.vladyslavbabenko.mycoloroflife.service.ArticleService;
import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import com.github.vladyslavbabenko.mycoloroflife.util.MessageSourceUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link Controller} to manage articles.
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {

    private final UserService userService;
    private final ArticleService articleService;
    private final MessageSourceUtil messageSource;

    private final String REDIRECT_ARTICLE = "redirect:/article";

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

        return messageSource.getMessage("template.general.article.all");
    }

    @GetMapping("/{articleId}")
    public String getArticle(@PathVariable("articleId") Integer articleId, Model model) {

        Optional<Article> optionalArticle = articleService.optionalFindById(articleId);

        if(optionalArticle.isEmpty()){
            model.addAttribute("articleNotFound", messageSource.getMessage("article.exists.not"));
            return messageSource.getMessage("template.general.article");
        }

        model.addAttribute("article", optionalArticle.get());
        return messageSource.getMessage("template.general.article");
    }

    @GetMapping("/new")
    public String getAddArticle(Model model) {
        model.addAttribute("article", new Article());
        return messageSource.getMessage("template.author.article.add");
    }

    @PostMapping()
    public String postAddArticle(@ModelAttribute @Valid Article article, BindingResult bindingResult, Model model) {
        article.setUsers(Collections.singleton(userService.getCurrentUser()));

        if (bindingResult.hasErrors()) {
            return messageSource.getMessage("template.author.article.add");
        }

        if (!articleService.saveArticle(article)) {
            model.addAttribute("articleExistsAlready", messageSource.getMessage("article.exists.already"));
            return messageSource.getMessage("template.author.article.add");
        }

        return getArticles(model,"",1);
    }

    @DeleteMapping("/{articleId}")
    public String deleteArticle(@PathVariable("articleId") Integer articleId) {
        boolean hasRole = userService.getCurrentUser().getRoles().stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase(messageSource.getMessage("role.admin")) ||
                        role.getRoleName().equalsIgnoreCase(messageSource.getMessage("role.author")));

        if (hasRole) {
            articleService.deleteArticle(articleId);
        }

        return REDIRECT_ARTICLE;
    }

    @GetMapping("/{articleId}/edit")
    public String getArticleEdit(@PathVariable("articleId") Integer articleId, Model model) {

        Optional<Article> optionalArticle = articleService.optionalFindById(articleId);

        if(optionalArticle.isEmpty()){
            model.addAttribute("articleNotFound", messageSource.getMessage("article.exists.not"));
            return messageSource.getMessage("template.general.article");
        }

        boolean isAdmin = userService.getCurrentUser().getRoles().stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase(messageSource.getMessage("role.admin")));

        if (optionalArticle.get().getUsers().contains(userService.getCurrentUser()) || isAdmin) {
            model.addAttribute("article", optionalArticle.get());
            return messageSource.getMessage("template.author.article.edit");
        } else {
            return messageSource.getMessage("template.error.access-denied");
        }
    }

    @PutMapping()
    public String putArticleUpdate(@ModelAttribute @Valid Article article, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return messageSource.getMessage("template.author.article.edit");
        }

        if (!articleService.updateArticle(article)) {
            model.addAttribute("articleNotFound", messageSource.getMessage("article.exists.not"));
            return messageSource.getMessage("template.author.article.edit");
        }

        return getArticles(model,"",1);
    }
}