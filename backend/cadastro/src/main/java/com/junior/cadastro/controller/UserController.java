package com.junior.cadastro.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.junior.cadastro.DTO.UserDTO;
import com.junior.cadastro.DTO.UserInsertDTO;
import com.junior.cadastro.exceptions.ApiError;
import com.junior.cadastro.service.UserService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping(value = "/user")
@Tag(name = "Usuários", description = "Endpoints de gerenciamento de usuários")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

	private final UserService service;

	public UserController(UserService service) {
		this.service = service;
	}

	@ApiResponse(responseCode = "200", description = "Usuários retornados com sucesso", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))))
	@ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "500", description = "Erro interno inesperado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	 @GetMapping
	public ResponseEntity<List<UserDTO>> findAll() {
		List<UserDTO> list = service.findAll();
		return ResponseEntity.ok().body(list);
	}

	@ApiResponse(responseCode = "200", description = "Usuário encontrado", content = @Content(schema = @Schema(implementation = UserDTO.class)))
	@ApiResponse(responseCode = "400", description = "Parâmetro id inválido", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "500", description = "Erro interno inesperado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@GetMapping(value = "/{id}")
	public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
		UserDTO user = service.findById(id);
		return ResponseEntity.ok().body(user);
	}

	@ApiResponse(responseCode = "201", description = "Usuário criado com sucesso", content = @Content(schema = @Schema(implementation = UserDTO.class)))
	@ApiResponse(responseCode = "400", description = "JSON inválido ou corpo da requisição malformado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "409", description = "E-mail já cadastrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "500", description = "Erro interno inesperado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PostMapping
	public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserInsertDTO dto) {
		UserDTO newDto = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newDto.getId()).toUri();
		return ResponseEntity.created(uri).body(newDto);
	}


	@ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso", content = @Content(schema = @Schema(implementation = UserDTO.class)))
	@ApiResponse(responseCode = "400", description = "Parâmetro inválido, JSON inválido ou erro de banco", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "404", description = "Usuário ou role não encontrada", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "409", description = "E-mail já cadastrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "422", description = "Erro de validação nos campos enviados", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "500", description = "Erro interno inesperado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@PutMapping(value = "/{id}")
	public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
		dto = service.update(id, dto);
		return ResponseEntity.ok().body(dto);
	}
	
	@ApiResponse(responseCode = "204", description = "Usuário removido com sucesso", content = @Content)
	@ApiResponse(responseCode = "400", description = "Parâmetro inválido ou usuário relacionado a outros registros", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@ApiResponse(responseCode = "500", description = "Erro interno inesperado", content = @Content(schema = @Schema(implementation = ApiError.class)))
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<UserDTO> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}


}