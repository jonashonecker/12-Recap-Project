package com.github.jonashonecker.backend;

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

    @PostMapping
    public Item addItem (@RequestBody ItemDTO newItem) {
        Item itemToAdd = new Item(null, newItem.description(), newItem.status());
        return superKanbanService.addItem(itemToAdd);
    }
}
