package eu.rigeldev.kuberig.dsl.kubernetes;

public class GitHubTagRef {

    private String ref;
    private String node_id;
    private String url;
    private GitHubGitObject object;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public GitHubGitObject getObject() {
        return object;
    }

    public void setObject(GitHubGitObject object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "GitHubTagRef{" +
                "ref='" + ref + '\'' +
                ", node_id='" + node_id + '\'' +
                ", url='" + url + '\'' +
                ", object=" + object +
                '}';
    }
}
