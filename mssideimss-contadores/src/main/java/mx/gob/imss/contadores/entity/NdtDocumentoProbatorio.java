package mx.gob.imss.contadores.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NDT_DOCUMENTO_PROBATORIO", schema = "MGPBDTU9X")
public class NdtDocumentoProbatorio {
    @Id
    @SequenceGenerator(name = "seq_doc", sequenceName = "MGPBDTU9X.SEQ_NDTDOCUMENTOPROBATORIO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_doc")
    @Column(name = "CVE_ID_DOCTO_PROBATORIO")
    private Long cveIdDoctoProbatorio;

    @Column(name = "CVE_ID_CPA")
    private Long cveIdCpa;

    @Column(name = "URL_DOCUMENTO_PROB")
    private String urlDocumentoProb;

    @Column(name = "CVE_ID_DOCTO_PROB_POR_TIPO")
    private Long cveIdDoctoProbPorTipo;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDateTime fecRegistroAlta;

    @Column(name = "CVE_ID_USUARIO")
    private String cveIdUsuario;
}