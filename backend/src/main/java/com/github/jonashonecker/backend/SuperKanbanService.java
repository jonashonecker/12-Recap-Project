package com.github.jonashonecker.backend;

import com.github.jonashonecker.backend.exceptions.IdNotFoundException;
import com.github.jonashonecker.backend.repo.Item;
import com.github.jonashonecker.backend.repo.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SuperKanbanService {
    private final ItemRepository itemRepository;
    private final UUIDService uuidService;

    public SuperKanbanService(ItemRepository itemRepository, UUIDService uuidService) {
        this.itemRepository = itemRepository;
        this.uuidService = uuidService;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item addItem(Item item) {
        Item newItem = item.withId(uuidService.generate());
        return itemRepository.insert(item);
    }

    public Item getItem(String id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("The requested Id: " + id + " does not exist."));
    }

    public Item updateItem(Item itemToUpdate) {
        return itemRepository.save(itemToUpdate);
    }

    public void deleteItemById(String id) {
        itemRepository.deleteById(id);
    }
}
