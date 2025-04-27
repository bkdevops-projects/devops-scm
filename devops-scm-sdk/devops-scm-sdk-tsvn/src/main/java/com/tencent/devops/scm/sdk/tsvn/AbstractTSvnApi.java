package com.tencent.devops.scm.sdk.tsvn;

import com.tencent.devops.scm.sdk.common.util.UrlEncoder;

public class AbstractTSvnApi implements TSvnConstants {

    protected final TSvnApi tSvnApi;

    public AbstractTSvnApi(TSvnApi tSvnApi) {
        this.tSvnApi = tSvnApi;
    }

    /**
     * 转换obj对象获取项目ID或者仓库名
     *
     * @param obj 仓库ID/仓库名/TGitProject对象
     * @return 项目ID或仓库名
     */
    public String getProjectIdOrPath(Object obj) {
        if (obj == null) {
            throw new TSvnApiException("Cannot determine ID or path from null object");
        } else if (obj instanceof Long) {
            return obj.toString();
        } else if (obj instanceof String) {
            return urlEncode((String) obj);
        } else {
            throw new TSvnApiException("Cannot determine ID or path from provided " + obj.getClass().getSimpleName()
                    + " instance, must be Integer, String, or a Project instance");
        }
    }

    protected String urlEncode(String s) {
        return UrlEncoder.urlEncode(s);
    }
}
