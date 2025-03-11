package com.tencent.devops.scm.sdk.tgit.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tencent.devops.scm.sdk.tgit.enums.TGitBlobType;
import com.tencent.devops.scm.sdk.tgit.enums.TGitEncoding;
import java.util.Base64;
import lombok.Data;

/**
 * 文件信息
 */
@Data
public class TGitRepositoryFile {

    private String fileName;
    private String filePath;
    private Integer size;
    private String ref;
    private String blobId;
    private TGitBlobType blobType;
    private String blobMode;
    private String commitId;
    private String content;
    private TGitEncoding encoding;

    @JsonIgnore
    public String getDecodedContentAsString() {

        if (content == null) {
            return null;
        }

        if (TGitEncoding.BASE64.equals(encoding)) {
            return new String(Base64.getDecoder().decode(content));
        }

        return content;
    }

    @JsonIgnore
    public byte[] getDecodedContentAsBytes() {

        if (content == null) {
            return null;
        }

        if (encoding == TGitEncoding.BASE64) {
            return Base64.getDecoder().decode(content);
        }

        return content.getBytes();
    }

    @JsonIgnore
    public void encodeAndSetContent(String content) {
        encodeAndSetContent(content != null ? content.getBytes() : null);
    }

    @JsonIgnore
    public void encodeAndSetContent(byte[] byteContent) {

        if (byteContent == null) {
            this.content = null;
            return;
        }

        this.content = Base64.getEncoder().encodeToString(byteContent);
        encoding = TGitEncoding.BASE64;
    }
}
