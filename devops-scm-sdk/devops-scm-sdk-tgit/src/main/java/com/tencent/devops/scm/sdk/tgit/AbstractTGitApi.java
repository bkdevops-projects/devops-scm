package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.util.UrlEncoder;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProject;

public class AbstractTGitApi implements TGitConstants {

    protected final TGitApi tGitApi;

    public AbstractTGitApi(TGitApi tGitApi) {
        this.tGitApi = tGitApi;
    }

    /**
     * 转换obj对象获取项目ID或者仓库名
     *
     * @param obj 仓库ID/仓库名/TGitProject对象
     * @return 项目ID或仓库名
     */
    public String getProjectIdOrPath(Object obj) {
        if (obj == null) {
            throw new TGitApiException("Cannot determine ID or path from null object");
        } else if (obj instanceof Long) {
            return obj.toString();
        } else if (obj instanceof String) {
            return urlEncode((String) obj);
        } else if (obj instanceof TGitProject) {
            Long id = ((TGitProject) obj).getId();
            if (id != null && id > 0) {
                return id.toString();
            }
            String path = ((TGitProject) obj).getPathWithNamespace();
            if (path != null && !path.isEmpty()) {
                return urlEncode(path);
            }
            throw new TGitApiException("Cannot determine ID or path from provided Project instance");
        } else {
            throw new TGitApiException("Cannot determine ID or path from provided " + obj.getClass().getSimpleName()
                    + " instance, must be Integer, String, or a Project instance");
        }
    }

    protected String urlEncode(String s) {
        return UrlEncoder.urlEncode(s);
    }
}
