package br.insper.grupo_tres.repository;


import br.insper.grupo_tres.models.CertificadoAtivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository 
public interface CertificadoAtivoRepository extends JpaRepository<CertificadoAtivo, Long> { 

    
    List<CertificadoAtivo> findByIdCurso(Long idCurso);

    
    Optional<CertificadoAtivo> findByEmailAlunoAndIdCurso(String emailAluno, Long idCurso);

    
}
