package com.github.jonashonecker.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.jonashonecker.backend.repo.Item;
import com.github.jonashonecker.backend.repo.ItemDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
public class SuperKanbanController {

    private final SuperKanbanService superKanbanService;

    public SuperKanbanController(SuperKanbanService superKanbanService) {
        this.superKanbanService = superKanbanService;
    }

    @GetMapping
    public List<Item> getAllItems () {
        return superKanbanService.getAllItems();
    }

    @GetMapping("{id}")
    public Item getItem (@PathVariable String id) {
        return superKanbanService.getItem(id);
    }

    @PostMapping
    public Item addItem (@RequestBody ItemDTO newItem) {
        Item itemToAdd = new Item(null, newItem.description(), newItem.status());
        return superKanbanService.addItem(itemToAdd);
    }

    @PutMapping("{id}")
    public Item updateItem (@RequestBody Item itemToUpdate) {
        return superKanbanService.updateItem(itemToUpdate);
    }

    @DeleteMapping("{id}")
    public void deleteItemById (@PathVariable String id) {
        superKanbanService.deleteItemById(id);
    }
}
