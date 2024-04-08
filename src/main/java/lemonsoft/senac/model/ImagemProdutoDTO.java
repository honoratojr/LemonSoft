package lemonsoft.senac.model;

import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;

import lombok.Data;

@Data
public class ImagemProdutoDTO {
    
    private Integer id;
    
    private String nomeArquivo;

    private MultipartFile arquivo;
    
    private byte[] arquivoBytes;

    private int ordenacao;
    
    private boolean principal;

    public byte[] getArquivoBytes() {
		return arquivoBytes;
	}

	public String getArquivoBytesBase64() {
		return Base64.getEncoder().encodeToString(this.arquivoBytes);
	}

	public void setArquivoBytes(byte[] arquivoBytes) {
		this.arquivoBytes = arquivoBytes;
	}

}