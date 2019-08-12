package br.gov.sp.prodesp.ssp.dipol.fotoservice.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import br.gov.sp.prodesp.ssp.dipol.commons.tusserver.model.TusFile;
import br.gov.sp.prodesp.ssp.dipol.fotoservice.enumeration.ExpiraCacheFotoEnum;
import br.gov.sp.prodesp.ssp.dipol.fotoservice.enumeration.ExtensaoFotoEnum;
import br.gov.sp.prodesp.ssp.dipol.fotoservice.enumeration.OrigemFotoEnum;
import br.gov.sp.prodesp.ssp.dipol.fotoservice.enumeration.ParteCorpoEnum;
import br.gov.sp.prodesp.ssp.dipol.fotoservice.enumeration.PlanoCorpoEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@RedisHash("foto")
public class FotoVO extends TusFile implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String UF = "uf";
	public static final String RG = "rg";
	public static final String CPF = "cpf";
	public static final String NOME = "nome";
	public static final String SEXO = "sexo";
	public static final String TAGS = "tags";
	public static final String PLANO = "plano";
	public static final String NOME_MAE = "nomeMae";	
	public static final String NOME_PAI = "nomePai";
	public static final String IS_PERFIL = "isPerfil";
	public static final String PARTE_CORPO = "parteCorpo";
	public static final String CODIGO_ORIGEM = "codigoOrigem";
	public static final String DATA_NASCIMENTO = "dataNascimento";

	@Id
	private String id;
	private String base64;
	private Boolean isPerfil;
	private PessoaVO pessoa;

	@Indexed
	private Integer codigoOrigem;
	private OrigemFotoEnum origem;

	private Integer parte;
	private ParteCorpoEnum parteCorpo;

	private Integer plano;
	private PlanoCorpoEnum planoCorpo;

	@Indexed
	private String cpf;

	@Indexed
	private String rg;

	@TimeToLive
	private Long expira;

	private List<FotoTagVO> tags;

	private ExpiraCacheFotoEnum expiraEnum;

	public FotoVO(String base64, OrigemFotoEnum origem) {
		id = UUID.randomUUID().toString();
		this.base64 = base64;
		codigoOrigem = origem.getId();
		this.origem = origem;
		expiraEnum = ExpiraCacheFotoEnum.DIA;
		setExtension(ExtensaoFotoEnum.JPG.getId());
		expira = ExpiraCacheFotoEnum.DIA.getExpiraSegundos();
	}

	public FotoVO(byte[] bytes, OrigemFotoEnum origem) {
		setBytes(bytes);
		id = UUID.randomUUID().toString();
		codigoOrigem = origem.getId();
		this.origem = origem;
		expiraEnum = ExpiraCacheFotoEnum.DIA;
		setExtension(ExtensaoFotoEnum.JPG.getId());
		expira = ExpiraCacheFotoEnum.DIA.getExpiraSegundos();
	}

	public FotoVO(String base64, OrigemFotoEnum origem, String rg, String cpf) {
		this(base64, origem);
		this.rg = rg;
		this.cpf = cpf;
		expiraEnum = ExpiraCacheFotoEnum.DIA;
		expira = ExpiraCacheFotoEnum.DIA.getExpiraSegundos();
	}
	
	public FotoVO(String base64, OrigemFotoEnum origem, String rg, String cpf, ExpiraCacheFotoEnum expiraEnum) {
		this(base64, origem, rg, cpf);
		this.expiraEnum = expiraEnum;
		expira = expiraEnum.getExpiraSegundos();
	}

	public FotoVO(Map<String, String> metadadosFoto, List<FotoTagVO> tags) {
		String uuid = UUID.randomUUID().toString();
		setId(uuid);
		setRg(metadadosFoto.get(RG));
		setCpf(metadadosFoto.get(CPF));
		setIsPerfil(Boolean.valueOf(metadadosFoto.get(IS_PERFIL)));

		if (!isPerfil) {

			Integer codPlano = Integer.valueOf(metadadosFoto.get(PLANO));
			setPlano(codPlano);
			setPlanoCorpo(PlanoCorpoEnum.valueOfId(codPlano));

			Integer codParte = Integer.valueOf(metadadosFoto.get(PARTE_CORPO));
			setParte(codParte);
			setParteCorpo(ParteCorpoEnum.valueOfId(codParte));
		}

		Integer codOrigem = Integer.valueOf(metadadosFoto.get(CODIGO_ORIGEM));
		setCodigoOrigem(codOrigem);
		setOrigem(OrigemFotoEnum.valueOfId(codOrigem));

		setExpiraEnum(ExpiraCacheFotoEnum.DIA);
		setExpira(ExpiraCacheFotoEnum.DIA.getExpiraSegundos());
		setTags(tags);

		String data = metadadosFoto.get(DATA_NASCIMENTO);
		// Pessoa
		setPessoa(PessoaVO.builder()
			.uf(metadadosFoto.get(UF))
			.nome(metadadosFoto.get(NOME))
			.sexo(metadadosFoto.get(SEXO))
			.nomeMae(metadadosFoto.get(NOME_MAE))
			.nomePai(metadadosFoto.get(NOME_PAI))
			.dataNascimento(StringUtils.isNotBlank(data) ? LocalDate.parse(data, DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null)
			.build());

		// Tus
		setOffset(0L);
		setUuid(uuid);
		setIsCompleted(false);
		setHash(metadadosFoto.get(HASH));
		setExtension(metadadosFoto.get(EXTENSION));
		setUploadLength(Long.valueOf(metadadosFoto.get(UPLOAD_LENGTH)));

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime newDate = now.plus(ExpiraCacheFotoEnum.DIA.getExpiraSegundos(), ChronoField.SECOND_OF_DAY.getBaseUnit());

		setDateExpiration(newDate);
	}

	@Builder
	public FotoVO(String uuid, String hash, String fileName, String extension, Long uploadLength, Long offset,
			byte[] bytes, Boolean isCompleted, LocalDateTime dateExpiration, String id, String base64, Boolean isPerfil,
			PessoaVO pessoa, OrigemFotoEnum origem, ParteCorpoEnum parteCorpo,
			PlanoCorpoEnum planoCorpo, String cpf, String rg, List<FotoTagVO> tags,
			ExpiraCacheFotoEnum expiraEnum) {
		
		this.id = StringUtils.isEmpty(id) ? UUID.randomUUID().toString() : id;
		this.base64 = base64;
		this.isPerfil = isPerfil;
		this.pessoa = pessoa;
		
		this.origem = origem;
		this.codigoOrigem = Objects.nonNull(origem) ? origem.getId() : null;
		
		this.parte = Objects.nonNull(parteCorpo) ? parteCorpo.getId() : null;
		this.parteCorpo = parteCorpo;
		
		this.plano = Objects.nonNull(planoCorpo) ? planoCorpo.getId() : null;
		this.planoCorpo = planoCorpo;
		
		this.tags = tags;
		this.cpf = cpf;
		this.rg = rg;
		this.expiraEnum = Objects.nonNull(expiraEnum) ? expiraEnum : ExpiraCacheFotoEnum.DIA;
		this.expira = this.expiraEnum.getExpiraSegundos();
		
		//Tus
		this.uuid = StringUtils.isEmpty(uuid) ? this.id : uuid; 
		this.hash = hash;
		this.fileName = fileName;
		this.extension= StringUtils.isEmpty(extension) ? ExtensaoFotoEnum.JPG.getId() : extension;
		this.uploadLength = uploadLength;
		this.offset = uploadLength;
		this.bytes=bytes;
		this.isCompleted=isCompleted;
		this.dateExpiration=dateExpiration;
	}
}
