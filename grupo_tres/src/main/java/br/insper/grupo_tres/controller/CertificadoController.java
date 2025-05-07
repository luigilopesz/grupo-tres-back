package br.insper.grupo_tres.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.insper.grupo_tres.dto.CursoDTO;
import br.insper.grupo_tres.models.CertificadoAtivo;
import br.insper.grupo_tres.models.CertificadoCancelado;
import br.insper.grupo_tres.service.CertificadoService;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;



@RestController
@RequestMapping("/certificados")
public class CertificadoController {

    @Autowired
    private CertificadoService certificadoService;

    // POST - Criar um novo certificado ativo
    @PostMapping
    public ResponseEntity<?> createCertificado(@RequestBody Map<String, Object> payload, @AuthenticationPrincipal Jwt jwt) {
        try {
            List<String> roles = jwt.getClaimAsStringList("https://musica-insper.com/roles");
            if (!roles.contains("ADMIN")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            String email = (String) payload.get("emailAluno");
            Long idCurso = Long.valueOf(payload.get("idCurso").toString());
            LocalDate dataEmissao = payload.containsKey("dataEmissao")
                    ? LocalDate.parse(payload.get("dataEmissao").toString())
                    : LocalDate.now();

            CertificadoAtivo cert = certificadoService.criarCertificado(email, idCurso, dataEmissao);
            return ResponseEntity.status(HttpStatus.CREATED).body(cert);

        } catch (IllegalArgumentException ex) {
            // usado para “não matriculado”
            return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno: " + ex.getMessage()));
        }
    }

    // GET - Buscar certificado ativo por ID
    @GetMapping("/{id}")
    public ResponseEntity<CertificadoAtivo> getCertificadoAtivo(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        try {
            List<String> roles = jwt.getClaimAsStringList("https://musica-insper.com/roles");
            if (!roles.contains("ADMIN")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            CertificadoAtivo certificado = certificadoService.findCertificadoAtivo(id);
            return ResponseEntity.ok(certificado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET - Buscar certificado cancelado por ID original
    @GetMapping("/cancelados/original/{idOriginal}")
    public ResponseEntity<CertificadoCancelado> getCertificadoCancelado(@PathVariable Long idOriginal, @AuthenticationPrincipal Jwt jwt) {
        try {
            List<String> roles = jwt.getClaimAsStringList("https://musica-insper.com/roles");
            if (!roles.contains("ADMIN")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            CertificadoCancelado certificado = certificadoService.findCertificadoCancelado(idOriginal);
            return ResponseEntity.ok(certificado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET - Listar todos os certificados ativos
    @GetMapping("/ativos")
    public ResponseEntity<List<CertificadoAtivo>> getAllCertificadosAtivos(@AuthenticationPrincipal Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("https://musica-insper.com/roles");
        if (!roles.contains("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        List<CertificadoAtivo> certificados = certificadoService.listarCertificadosAtivos();
        return ResponseEntity.ok(certificados);
    }

    // GET - Listar todos os certificados cancelados
    @GetMapping("/cancelados")
    public ResponseEntity<List<CertificadoCancelado>> getAllCertificadosCancelados(@AuthenticationPrincipal Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("https://musica-insper.com/roles");
        if (!roles.contains("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        List<CertificadoCancelado> certificados = certificadoService.listarCertificadosCancelados();
        return ResponseEntity.ok(certificados);
    }

    // GET - Listar certificados ativos por ID do curso
    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<CertificadoAtivo>> getCertificadosPorCurso(@PathVariable Long idCurso, @AuthenticationPrincipal Jwt jwt) {
        try {
            List<String> roles = jwt.getClaimAsStringList("https://musica-insper.com/roles");
            if (!roles.contains("ADMIN")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            List<CertificadoAtivo> certificados = certificadoService.listarCertificadosPorCurso(idCurso);
            return ResponseEntity.ok(certificados);
        } catch (RuntimeException e) {
            // Consider returning an empty list with OK status or a specific status like NO_CONTENT
            // Depending on API design preference. Here, returning OK with empty list.
            return ResponseEntity.ok(List.of());
        }
    }

    // POST - Cancelar um certificado ativo
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarCertificado(@PathVariable Long id, @RequestBody Map<String, String> payload, @AuthenticationPrincipal Jwt jwt) {
        String motivo = payload.get("motivo");
        if (motivo == null || motivo.trim().isEmpty()) {
            return ResponseEntity.badRequest().build(); // Motivo is required
        }
        try {
            List<String> roles = jwt.getClaimAsStringList("https://musica-insper.com/roles");
            if (!roles.contains("ADMIN")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            certificadoService.cancelarCertificado(id, motivo);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            // Differentiate between "not found" and "already canceled" if needed
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Or NotFound, depending on the exception message
        }
    }

    @GetMapping("/curso/{idCurso}/info")
    public ResponseEntity<CursoDTO> getInfoCurso(@PathVariable Long idCurso, @AuthenticationPrincipal Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("https://musica-insper.com/roles");
        if (!roles.contains("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        CursoDTO curso = certificadoService.buscarCurso(idCurso);
        return ResponseEntity.ok(curso);
    }
}