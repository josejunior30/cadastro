package com.junior.cadastro.service;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junior.cadastro.DTO.ConnectTokenResponse;
import com.junior.cadastro.entities.PluggyItem;
import com.junior.cadastro.entities.User;
import com.junior.cadastro.exceptions.PluggyIntegrationException;
import com.junior.cadastro.repository.PluggyAccountRepository;
import com.junior.cadastro.repository.PluggyItemRepository;
import com.junior.cadastro.repository.PluggyTransactionRepository;
import com.junior.cadastro.repository.UserRepository;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class PluggyServiceTest {

    @Mock
    private PluggyMapper pluggyMapper;

    @Mock
    private PluggyClientService pluggyClientService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PluggyItemRepository itemRepository;

    @Mock
    private PluggyAccountRepository accountRepository;

    @Mock
    private PluggyTransactionRepository transactionRepository;

    @InjectMocks
    private PluggyService service;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private User authenticatedUser;

    @BeforeEach
    void setUp() {
        authenticatedUser = new User(1L, "José", "Junior", "jose@email.com", "123");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("jose@email.com", null)
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createConnectTokenShouldUseAuthenticatedUser() {
        when(userRepository.findByEmail("jose@email.com")).thenReturn(Optional.of(authenticatedUser));
        when(pluggyClientService.createConnectToken("1")).thenReturn("pluggy-token");

        ConnectTokenResponse response = service.createConnectToken();

        assertNotNull(response);
        verify(pluggyClientService).createConnectToken("1");
    }

    @Test
    void syncItemShouldMarkSuccessWhenPluggyReturnsNoAccounts() throws Exception {
        when(userRepository.findByEmail("jose@email.com")).thenReturn(Optional.of(authenticatedUser));
        when(itemRepository.findByPluggyItemId("item-1")).thenReturn(Optional.empty());
        when(itemRepository.save(any(PluggyItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pluggyClientService.fetchAccounts("item-1")).thenReturn(
                objectMapper.readTree("{\"results\":[]}")
        );

        service.syncItem("item-1");

        ArgumentCaptor<PluggyItem> captor = ArgumentCaptor.forClass(PluggyItem.class);
        verify(itemRepository, atLeast(2)).save(captor.capture());

        PluggyItem lastSaved = captor.getAllValues().get(captor.getAllValues().size() - 1);
        assertNotNull(lastSaved.getLastSyncAt());
        verify(accountRepository, never()).findByPluggyAccountId(anyString());
        verify(pluggyClientService, never()).fetchTransactions(anyString(), anyInt());
    }

    @Test
    void syncItemShouldMarkErrorAndRethrowWhenPluggyFails() {
        when(userRepository.findByEmail("jose@email.com")).thenReturn(Optional.of(authenticatedUser));
        when(itemRepository.findByPluggyItemId("item-1")).thenReturn(Optional.empty());
        when(itemRepository.save(any(PluggyItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pluggyClientService.fetchAccounts("item-1"))
                .thenThrow(new PluggyIntegrationException("Falha ao buscar contas"));

        assertThrows(PluggyIntegrationException.class, () -> service.syncItem("item-1"));

        verify(itemRepository, atLeast(2)).save(any(PluggyItem.class));
    }

    @Test
    void findMyTransactionsByAccountShouldThrowWhenAccountDoesNotBelongToUser() {
        when(userRepository.findByEmail("jose@email.com")).thenReturn(Optional.of(authenticatedUser));
        when(accountRepository.findByIdAndUser(99L, authenticatedUser)).thenReturn(Optional.empty());

        assertThrows(
                PluggyIntegrationException.class,
                () -> service.findMyTransactionsByAccount(99L, PageRequest.of(0, 20))
        );

        verify(transactionRepository, never())
                .findByUserAndAccountIdOrderByDateDesc(any(), anyLong(), any());
    }
}