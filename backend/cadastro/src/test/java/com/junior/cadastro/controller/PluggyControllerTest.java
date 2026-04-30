package com.junior.cadastro.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import com.junior.cadastro.DTO.ConnectTokenResponse;
import com.junior.cadastro.DTO.PluggyAccountDTO;
import com.junior.cadastro.DTO.PluggySyncRequest;
import com.junior.cadastro.DTO.PluggyTransactionDTO;
import com.junior.cadastro.service.PluggyService;

@ExtendWith(MockitoExtension.class)
class PluggyControllerTest {

    @Mock
    private PluggyService pluggyService;

    private PluggyController controller;

    @BeforeEach
    void setUp() {
        controller = new PluggyController(pluggyService);
    }

    @Test
    void createConnectTokenShouldReturnOk() {
        ConnectTokenResponse expected = new ConnectTokenResponse("pluggy-token");
        when(pluggyService.createConnectToken()).thenReturn(expected);

        ResponseEntity<?> response = controller.createConnectToken();

        assertEquals(200, response.getStatusCode().value());
        assertSame(expected, response.getBody());
    }

    @Test
    void syncItemShouldReturnNoContent() {
        PluggySyncRequest request = mock(PluggySyncRequest.class);
        when(request.getItemId()).thenReturn("item-123");

        ResponseEntity<?> response = controller.syncItem(request);

        assertEquals(204, response.getStatusCode().value());
        verify(pluggyService).syncItem("item-123");
    }

    @Test
    void findMyAccountsShouldReturnOk() {
        List<PluggyAccountDTO> expected = List.of(mock(PluggyAccountDTO.class), mock(PluggyAccountDTO.class));
        when(pluggyService.findMyAccounts()).thenReturn(expected);

        ResponseEntity<?> response = controller.findMyAccounts();

        assertEquals(200, response.getStatusCode().value());
        assertSame(expected, response.getBody());
    }

    @Test
    void findMyTransactionsByAccountShouldReturnOk() {
        Page<PluggyTransactionDTO> expected = new PageImpl<>(List.of(mock(PluggyTransactionDTO.class)));
        when(pluggyService.findMyTransactionsByAccount(10L, PageRequest.of(0, 20))).thenReturn(expected);

        ResponseEntity<?> response = controller.findMyTransactionsByAccount(10L, PageRequest.of(0, 20));

        assertEquals(200, response.getStatusCode().value());
        assertSame(expected, response.getBody());
    }
}