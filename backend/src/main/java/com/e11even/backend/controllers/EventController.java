package com.e11even.backend.controllers;

import com.e11even.backend.models.Event;
import com.e11even.backend.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    // 1. ROUTE POUR LA LISTE (Accueil)
    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // 2. ROUTE POUR CRÉER (Formulaire)
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event savedEvent = eventRepository.save(event);
        return ResponseEntity.ok(savedEvent);
    }

    // 3. LA FAMEUSE ROUTE POUR LE DÉTAIL (Celle qui manquait peut-être !)
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(event -> ResponseEntity.ok().body(event))
                .orElse(ResponseEntity.notFound().build()); // C'est ici que ça renvoie 404 si l'ID n'existe pas
    }
}