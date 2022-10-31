package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.entity.Event;
import com.github.vladyslavbabenko.mycoloroflife.service.EventService;
import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import com.github.vladyslavbabenko.mycoloroflife.util.MessageSourceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link Controller} to manage events.
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {
    private final UserService userService;
    private final EventService eventService;
    private final MessageSourceUtil messageSource;

    private final String REDIRECT_EVENT = "redirect:/event";

    @GetMapping(path = {"", "/page/{pageId}"})
    public String getEvents(Model model, String keyword, @PathVariable(value = "pageId", required = false) Integer pageId) {
        if (pageId == null) {
            pageId = 1;
        }

        List<Event> events;

        if (keyword != null) {
            events = eventService.findByKeyword(keyword);
        } else {
            events = eventService.getAllEvents();
        }

        List<Event> listOfEvents = events.stream().skip(pageId * 6L - 6).limit(6).collect(Collectors.toList());

        int[] numberOfPages = new int[(int) Math.ceil(events.size() / 6.0)];

        for (int i = 0; i < numberOfPages.length; i++) {
            numberOfPages[i] = i;
        }

        model.addAttribute("listOfEvents", listOfEvents);
        model.addAttribute("pageID", pageId);
        model.addAttribute("numberOfPages", numberOfPages);

        return messageSource.getMessage("template.general.event.all");
    }

    @GetMapping("/{eventId}")
    public String getEvent(@PathVariable("eventId") Integer eventId, Model model) {
        model.addAttribute("event", eventService.findById(eventId));
        return messageSource.getMessage("template.general.event");
    }

    @GetMapping("/new")
    public String getAddEvent(Model model) {
        model.addAttribute("event", new Event());
        return messageSource.getMessage("template.author.event.add");
    }

    @PostMapping()
    public String postAddEvent(@ModelAttribute @Valid Event event, BindingResult bindingResult, Model model) {
        event.setUsers(Collections.singleton(userService.getCurrentUser()));

        if (bindingResult.hasErrors()) {
            return messageSource.getMessage("template.author.event.add");
        }

        if (!eventService.saveEvent(event)) {
            model.addAttribute("eventError", messageSource.getMessage("event.exists.already"));
            return messageSource.getMessage("template.author.event.add");
        }

        return REDIRECT_EVENT;
    }

    @DeleteMapping("/{eventId}")
    public String deleteEvent(@PathVariable("eventId") Integer eventId) {
        if (userService.getCurrentUser().getRoles().stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase(messageSource.getMessage("role.admin")) ||
                        role.getRoleName().equalsIgnoreCase(messageSource.getMessage("role.author")))) {
            eventService.deleteEvent(eventId);
        }
        return REDIRECT_EVENT;
    }

    @GetMapping("/{eventId}/edit")
    public String getEditEvent(@PathVariable("eventId") Integer eventId, Model model) {
        Event event = eventService.findById(eventId);

        boolean isAdmin = userService.getCurrentUser().getRoles().stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase(messageSource.getMessage("role.admin")));

        if (event.getUsers().contains(userService.getCurrentUser()) || isAdmin) {
            model.addAttribute("event", event);
            return messageSource.getMessage("template.author.event.edit");
        } else {
            return messageSource.getMessage("template.error.access-denied");
        }
    }

    @PutMapping()
    public String putEventUpdate(@ModelAttribute @Valid Event event, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return messageSource.getMessage("template.author.event.edit");
        }

        if (!eventService.updateEvent(event)) {
            model.addAttribute("updateEventError", messageSource.getMessage("event.exists.not"));
            return messageSource.getMessage("template.author.event.edit");
        }

        return REDIRECT_EVENT;
    }
}