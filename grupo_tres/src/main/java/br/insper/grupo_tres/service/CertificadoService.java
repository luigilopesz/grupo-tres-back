package br.insper.grupo_tres.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        return ativoRepository.findAll();
    }
    
    // GET ALL
    public List<CertificadoCancelado> listarCertificadosCancelados() {
        return canceladoRepository.findAll();
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
    public CertificadoAtivo criarCertificado(String emailAluno, Long idCurso, LocalDate dataEmissao) {
        
        //RestTemplate restTemplate = new RestTemplate();
        //ResponseEntity<String> response = restTemplate.getForEntity("rota get all matriculas", String.class);
        // if (!response.getStatusCode().equals(HttpStatus.OK)) {
        //     throw new RuntimeException("Failed to fetch matriculas: " + response.getStatusCode());
        // }
        
        // String matriculas = response.getBody();

        return ativoRepository.save(new CertificadoAtivo(emailAluno, idCurso, dataEmissao));
        
    }   
}
