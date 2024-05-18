package com.github.jonashonecker.backend;

import com.github.jonashonecker.backend.repo.Item;
import com.github.jonashonecker.backend.repo.ItemRepository;
import com.github.jonashonecker.backend.repo.Status;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SuperKanbanServiceTest {

    public ItemRepository mockItemRepository = mock();
    public UUIDService mockUUIDService = mock();
    public SuperKanbanService superKanbanService = new SuperKanbanService(mockItemRepository, mockUUIDService);

    @Test
    void getAllItems_returnListOfItems_whenCalled() {
        //GIVEN
        List<Item> expected = List.of(
                new Item("1", "abc", Status.OPEN),
                new Item("2", "abc", Status.OPEN),
                new Item("3", "abc", Status.OPEN)
        );

        when(mockItemRepository.findAll()).thenReturn(expected);

        //WHEN
        List<Item> actual = superKanbanService.getAllItems();

        //THEN
        verify(mockItemRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void addItem_returnItemWithId_whenCalledWithItem() {
        //GIVEN
        Item newitem = new Item(null, "this is a test", Status.OPEN);
        String id = "123";

        when(mockItemRepository.insert(newitem)).thenReturn(newitem.withId(id));
        when(mockUUIDService.generate()).thenReturn(id);

        //WHEN
        Item actual = superKanbanService.addItem(newitem);

        //THEN
        verify(mockItemRepository).insert(newitem);
        verify(mockUUIDService).generate();
        Item expected = newitem.withId(id);
        assertEquals(expected, actual);
    }
}