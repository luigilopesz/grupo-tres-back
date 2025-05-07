package br.insper.grupo_tres.repository;

import br.insper.grupo_tres.models.CertificadoCancelado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import br.insper.grupo_tres.models.StatusCertificado;

@Repository 
public interface CertificadoCanceladoRepository extends JpaRepository<CertificadoCancelado, Long> { 
    
    List<CertificadoCancelado> findByIdCurso(Long idCurso);
    List<CertificadoCancelado> findByStatus(StatusCertificado status);


    Optional<CertificadoCancelado> findByIdCertificadoOriginal(Long idCertificadoOriginal);

}
