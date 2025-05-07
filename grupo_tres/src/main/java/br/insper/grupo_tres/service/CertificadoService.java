package br.insper.grupo_tres.service;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import br.insper.grupo_tres.dto.CursoDTO;
import br.insper.grupo_tres.dto.MatriculaDTO;
import br.insper.grupo_tres.models.CertificadoAtivo;
import br.insper.grupo_tres.models.CertificadoCancelado;
import br.insper.grupo_tres.models.StatusCertificado;
import br.insper.grupo_tres.repository.CertificadoAtivoRepository;
import br.insper.grupo_tres.repository.CertificadoCanceladoRepository;


@Service
public class CertificadoService {


    @Autowired
    private CertificadoAtivoRepository ativoRepository;

    @Autowired
    private CertificadoCanceladoRepository canceladoRepository;


    // GET
    public CertificadoAtivo findCertificadoAtivo(Long idCertificado) {
        return ativoRepository.findById(idCertificado).orElseThrow(() -> new RuntimeException("Certificado não encontrado"));
    }

    // GET
    public CertificadoCancelado findCertificadoCancelado(Long idCertificadoOriginal) {
        return canceladoRepository.findByIdCertificadoOriginal(idCertificadoOriginal).orElseThrow(() -> new RuntimeException("Certificado não encontrado"));
    }

    // GET ALL
    public List<CertificadoAtivo> listarCertificadosAtivos() {
        return ativoRepository.findByStatus(StatusCertificado.ATIVO);
    }

    // GET ALL
    public List<CertificadoCancelado> listarCertificadosCancelados() {
        return canceladoRepository.findByStatus(StatusCertificado.CANCELADO);
    }

    // GET ALL BY CURSO
    public List<CertificadoAtivo> listarCertificadosPorCurso(Long idCurso) {
        List<CertificadoAtivo> ativos = ativoRepository.findByIdCurso(idCurso);
        if (ativos.isEmpty()) {
            throw new RuntimeException("Nenhum certificado encontrado para o curso com ID: " + idCurso);
        }
        return ativos;
    }

    // CANCELAR CERTIFICADO
    public void cancelarCertificado(Long idCertificado, String motivo) {

        CertificadoAtivo ativo = findCertificadoAtivo(idCertificado);
        if (ativo.getStatus() == StatusCertificado.CANCELADO) {
            throw new RuntimeException("Certificado já cancelado");
        }
        var cancelado = new CertificadoCancelado(ativo, motivo, LocalDate.now());
        ativo.setStatus(StatusCertificado.CANCELADO);
        canceladoRepository.save(cancelado);
    }

    // CREATE
    private static final String MATRICULA_API = "https://54.94.157.137:8082/matriculas/{id}";

    @Autowired
    private RestTemplate restTemplate;



    public CertificadoAtivo criarCertificado(String emailAluno, Long idCurso, LocalDate dataEmissao) {
        // 1. Monta a URL, expandindo {id} e adicionando o queryParam
        URI uri = UriComponentsBuilder
                .fromHttpUrl(MATRICULA_API)
                .queryParam("emailAluno", emailAluno)
                .buildAndExpand(idCurso)    // aqui injeta o idCurso no lugar de {id}
                .toUri();

        // 2. Chama a API de matrícula
        ResponseEntity<MatriculaDTO[]> resp;
        try {
            resp = restTemplate.getForEntity(uri, MatriculaDTO[].class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new IllegalArgumentException("Aluno não matriculado neste curso");
        } catch (RestClientException ex) {
            throw new RuntimeException("Falha ao consultar matrícula: " + ex.getMessage(), ex);
        }

        // 3. Verifica se retornou alguma matrícula válida
        MatriculaDTO[] matriculas = resp.getBody();
        if (matriculas == null || matriculas.length == 0) {
            throw new IllegalArgumentException("Aluno não matriculado neste curso");
        }

        // 4. Se estiver tudo OK, salva o certificado
        CertificadoAtivo cert = new CertificadoAtivo(emailAluno, idCurso, dataEmissao);
        return ativoRepository.save(cert);
    }

    private static final String CURSO_API = "http://54.232.22.180:8080/api/cursos/{id}";
    public CursoDTO buscarCurso(Long idCurso) {
        try {
            ResponseEntity<CursoDTO> resp =
                    restTemplate.getForEntity(CURSO_API, CursoDTO.class, idCurso);

            return resp.getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new IllegalArgumentException("Curso não encontrado: " + idCurso);
        } catch (RestClientException ex) {
            throw new RuntimeException("Erro ao chamar API de cursos: " + ex.getMessage(), ex);
            }
        }

}