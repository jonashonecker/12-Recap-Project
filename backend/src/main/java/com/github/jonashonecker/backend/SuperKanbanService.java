package com.github.jonashonecker.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jonashonecker.backend.exceptions.IdNotFoundException;
import com.github.jonashonecker.backend.ollamaGemma2b.GemmaRequest;
import com.github.jonashonecker.backend.ollamaGemma2b.GemmaResponse;
import com.github.jonashonecker.backend.ollamaGemma2b.ResponsePropertyJson;
import com.github.jonashonecker.backend.repo.Item;
import com.github.jonashonecker.backend.repo.ItemRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class SuperKanbanService {
    private final ItemRepository itemRepository;
    private final UUIDService uuidService;
    private final GemmaAIService gemmaAIService;

    public SuperKanbanService(ItemRepository itemRepository, UUIDService uuidService, GemmaAIService gemmaAIService) {
        this.itemRepository = itemRepository;
        this.uuidService = uuidService;
        this.gemmaAIService = gemmaAIService;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item addItem(Item item) {
        Item newItem = item.withId(uuidService.generate());
        Item newItemWithCorrectedDescription = newItem.withDescription(gemmaAIService.improveTextQuality(newItem.description()));
        return itemRepository.insert(newItemWithCorrectedDescription);
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
