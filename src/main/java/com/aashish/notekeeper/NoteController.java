package com.aashish.notekeeper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final Map<Long, Note> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @GetMapping
    public List<Note> list() {
        return List.copyOf(store.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> get(@PathVariable Long id) {
        Note note = store.get(id);
        return note == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(note);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Note create(@RequestBody Note incoming) {
        long id = sequence.incrementAndGet();
        Note saved = new Note(id, incoming.title(), incoming.body());
        store.put(id, saved);
        return saved;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return store.remove(id) == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.noContent().build();
    }
}