package br.com.fiap.techchallenge.hospital.scheduling.api.users;

import br.com.fiap.techchallenge.hospital.scheduling.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Cadastro de usuarios do sistema")
public class UserAccountController {

    private final UserAccountService userAccountService;

    @PostMapping
    @Operation(summary = "Cadastrar usuario", description = "Cria um novo usuario (medico, enfermeiro ou paciente). Endpoint publico.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario criado",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload invalido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Username ja existe",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        var response = userAccountService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/users/" + response.getId()))
                .body(response);
    }
}
