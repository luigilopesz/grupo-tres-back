package br.insper.grupo_tres.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatriculaDTO {
    private String emailAluno;
    private Long idCurso;
    private LocalDate dataMatricula;

}