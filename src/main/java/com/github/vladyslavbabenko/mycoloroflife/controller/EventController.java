package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.entity.Event;
import com.github.vladyslavbabenko.mycoloroflife.service.EventService;
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
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;
    private final UserService userService;

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

        return "generalTemplate/eventsPage";
    }

    @GetMapping("/{eventId}")
    public String getEventById(@PathVariable("eventId") Integer eventId, Model model) {
        model.addAttribute("event", eventService.findById(eventId));
        return "generalTemplate/eventPage";
    }

    @GetMapping("/new")
    public String newEvent(Model model) {
        model.addAttribute("event", new Event());
        return "authorTemplate/newEventPage";
    }

    @PostMapping()
    public String createEvent(@ModelAttribute @Valid Event event, BindingResult bindingResult, Model model) {
        event.setUsers(Collections.singleton(userService.getCurrentUser()));

        if (bindingResult.hasErrors()) {
            return "authorTemplate/newEventPage";
        }

        if (!eventService.saveEvent(event)) {
            model.addAttribute("eventError", "Така подія вже існує");
            return "authorTemplate/newEventPage";
        }

        return "redirect:/event";
    }

    @DeleteMapping("/{eventId}")
    public String deleteEvent(@PathVariable("eventId") Integer eventId) {
        if (userService.getCurrentUser().getRoles().stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase("ROLE_ADMIN") ||
                        role.getRoleName().equalsIgnoreCase("ROLE_AUTHOR"))) {
            eventService.deleteEvent(eventId);
        }
        return "redirect:/event";
    }

    @GetMapping("/{eventId}/edit")
    public String toEventEdit(@PathVariable("eventId") Integer eventId, Model model) {
        Event event = eventService.findById(eventId);

        boolean isAdmin = userService.getCurrentUser().getRoles().stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase("ROLE_ADMIN"));

        if (event.getUsers().contains(userService.getCurrentUser()) || isAdmin) {
            model.addAttribute("event", event);
            return "authorTemplate/editEventPage";
        } else {
            return "error/accessDeniedPage";
        }
    }

    @PutMapping()
    public String updateEvent(@ModelAttribute @Valid Event event, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "authorTemplate/editEventPage";
        }

        if (!eventService.updateEvent(event)) {
            model.addAttribute("updateEventError", "Такої події не існує");
            return "authorTemplate/editEventPage";
        }

        return "redirect:/event";
    }
}