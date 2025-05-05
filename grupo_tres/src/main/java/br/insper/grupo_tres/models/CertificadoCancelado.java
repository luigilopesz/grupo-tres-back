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
@Table(name = "certificados_cancelados") 
public class CertificadoCancelado {

    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "id_cancelamento") 
    private Long idCancelamento;

    
    @Column(name = "id_certificado_original", nullable = false, unique = true) 
    private Long idCertificadoOriginal;

    
    @Column(name = "email_aluno", nullable = false) 
    private String emailAluno;

    
    @Column(name = "id_curso", nullable = false) 
    private Long idCurso;

    
    @Column(name = "data_emissao", nullable = false) 
    private LocalDate dataEmissao;

    
    @Column(name = "motivo_cancelamento", nullable = false) 
    private String motivoCancelamento;

    
    @Column(name = "data_cancelamento", nullable = false) 
    private LocalDate dataCancelamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusCertificado status;

    
    public CertificadoCancelado(CertificadoAtivo ativo, String motivo, LocalDate dataCancelamento) {
        this.idCertificadoOriginal = ativo.getIdCertificado(); 
        this.emailAluno = ativo.getEmailAluno();
        this.idCurso = ativo.getIdCurso();
        this.dataEmissao = ativo.getDataEmissao();
        this.status = StatusCertificado.CANCELADO;
        this.motivoCancelamento = motivo;
        this.dataCancelamento = dataCancelamento;
    }

}

