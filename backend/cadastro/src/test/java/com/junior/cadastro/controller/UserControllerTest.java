package com.junior.cadastro.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.junior.cadastro.DTO.UserDTO;
import com.junior.cadastro.DTO.UserInsertDTO;
import com.junior.cadastro.service.UserService;

class UserControllerTest {

    private UserController controller;
    private UserService service;

    @BeforeEach
    void setUp() {
        controller = new UserController();
        service = mock(UserService.class);
        ReflectionTestUtils.setField(controller, "service", service);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void findAllShouldReturnOkWithBody() {
        List<UserDTO> expected = List.of(mock(UserDTO.class), mock(UserDTO.class));
        when(service.findAll()).thenReturn(expected);

        ResponseEntity<List<UserDTO>> response = controller.findAll();

        assertEquals(200, response.getStatusCode().value());
        assertSame(expected, response.getBody());
        verify(service).findAll();
    }

    @Test
    void findByIdShouldReturnOkWithBody() {
        UserDTO expected = mock(UserDTO.class);
        when(service.findById(1L)).thenReturn(expected);

        ResponseEntity<UserDTO> response = controller.findById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertSame(expected, response.getBody());
        verify(service).findById(1L);
    }

    @Test
    void insertShouldReturnCreatedWithLocationAndBody() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/user");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        UserInsertDTO inputDto = mock(UserInsertDTO.class);
        UserDTO createdDto = mock(UserDTO.class);

        when(createdDto.getId()).thenReturn(10L);
        when(service.insert(inputDto)).thenReturn(createdDto);

        ResponseEntity<UserDTO> response = controller.insert(inputDto);

        assertEquals(201, response.getStatusCode().value());
        assertSame(createdDto, response.getBody());
        assertEquals("http://localhost/user/10", response.getHeaders().getLocation().toString());
        verify(service).insert(inputDto);
    }

    @Test
    void deleteShouldReturnNoContent() {
        ResponseEntity<UserDTO> response = controller.delete(5L);

        assertEquals(204, response.getStatusCode().value());
        verify(service).delete(5L);
    }

    @Test
    void updateShouldReturnOkWithUpdatedBody() {
        UserDTO inputDto = mock(UserDTO.class);
        UserDTO updatedDto = mock(UserDTO.class);

        when(service.update(7L, inputDto)).thenReturn(updatedDto);

        ResponseEntity<UserDTO> response = controller.update(7L, inputDto);

        assertEquals(200, response.getStatusCode().value());
        assertSame(updatedDto, response.getBody());
        verify(service).update(7L, inputDto);
    }
}