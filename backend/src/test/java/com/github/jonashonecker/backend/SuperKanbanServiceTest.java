package com.github.jonashonecker.backend;

import com.github.jonashonecker.backend.exceptions.IdNotFoundException;
import com.github.jonashonecker.backend.repo.Item;
import com.github.jonashonecker.backend.repo.ItemRepository;
import com.github.jonashonecker.backend.repo.Status;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void getItem_throwsIdNotFoundException_whenItemNotInRepository() {
        //GIVEN
        when(mockItemRepository.findById(any())).thenReturn(Optional.empty());

        //WHEN
        IdNotFoundException actual = assertThrows(IdNotFoundException.class, () -> superKanbanService.getItem("1"));

        //THEN
        String expected = "The requested Id: 1 does not exist.";
        assertEquals(expected, actual.getMessage());
    }

    @Test
    void getItem_returnItemWithId_whenItemByIdRequested() {
        //GIVEN
        String id = "1";
        Item expected = new Item(id, "TEST", Status.IN_PROGRESS);

        when(mockItemRepository.findById(id)).thenReturn(Optional.of(expected));

        //WHEN
        Item actual = superKanbanService.getItem(id);

        //THEN
        assertEquals(expected, actual);
    }

    @Test
    void updateItem_returnChangedItem_whenItemPropertiesChanged() {
        //GIVEN
        Item expected = new Item("1", "Hello", Status.DONE);

        when(mockItemRepository.save(any())).thenReturn(expected);

        //WHEN
        Item actual = superKanbanService.updateItem(expected);

        //THEN
        assertEquals(expected, actual);
    }
}