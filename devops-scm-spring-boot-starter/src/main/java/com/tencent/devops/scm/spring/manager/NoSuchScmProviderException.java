

package com.tencent.devops.scm.spring.manager;

import static java.util.Objects.requireNonNull;

import com.tencent.devops.scm.api.exception.ScmApiException;
import lombok.Getter;

@Getter
public class NoSuchScmProviderException extends ScmApiException {

    private final String providerName;

    public NoSuchScmProviderException(String providerName) {
        super("No such provider: '" + providerName + "'.");
        this.providerName = requireNonNull(providerName);
    }
}
