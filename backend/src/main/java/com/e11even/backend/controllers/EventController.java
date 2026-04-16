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

    // 1. ROUTE POUR AFFICHER LA PAGE D'ACCUEIL (Récupérer la liste)
    @GetMapping
    public List<Event> getAllEvents() {
        // Va chercher tous les évènements dans la BDD et les renvoie au format JSON
        return eventRepository.findAll();
    }

    // 2. ROUTE POUR LE FORMULAIRE DE CRÉATION (Ajouter un évènement)
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        // Pour l'instant, on sauvegarde directement l'évènement reçu du Frontend
        Event savedEvent = eventRepository.save(event);
        
        // On renvoie l'évènement sauvegardé avec un code 200 OK
        return ResponseEntity.ok(savedEvent);
    }
}