## Emissao e cancelamento de certificados 

### Certificado

    - id_certificado
    - email_aluno
    - id_curso
    - data_emissao
    - status ( ATIVO | CANCELADO )

    --- cancelado ---  
    ( os certificados cancelados podem ser tanto um objeto a parte quanto campos com valores nulos no objeto principal ) 

    - id_certificado
    - motivo_cancelamento
    - data_cancelamento

### Rotas

    - criar certificado ( busca uma matricula existente com o status CONCLUIDO ) POST api/v1/certificado
    - listar todos os certificados de um curso  GET api/v1/certificado/<id_curso>
    - busca dos dados especificos de um certificado ( get passando id de certificado ) GET api/v1/certificado/<id_certificado>

    - cancelar um certificado ( ADMIN ) POST api/v1/certificado/cancel/<id_certificado>

