package br.insper.grupo_tres.models;

import jakarta.persistence.*; 
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;



@Getter 
@Setter
@NoArgsConstructor 
@Entity 
@Table(name = "certificados_ativos") 
public class CertificadoAtivo {

    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "id_certificado") 
    private Long idCertificado;

    
    @Column(name = "email_aluno", nullable = false) 
    private String emailAluno;

    
    @Column(name = "id_curso", nullable = false) 
    private Long idCurso; 

    
    @Column(name = "data_emissao", nullable = false) 
    private LocalDate dataEmissao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusCertificado status;

    
    public CertificadoAtivo(String emailAluno, Long idCurso, LocalDate dataEmissao) {
        this.emailAluno = emailAluno;
        this.idCurso = idCurso;
        this.dataEmissao = dataEmissao;
        this.status = StatusCertificado.ATIVO;
    }

}
