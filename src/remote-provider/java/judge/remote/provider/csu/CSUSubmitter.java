package judge.remote.provider.csu;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import judge.httpclient.DedicatedHttpClient;
import judge.httpclient.HttpStatusValidator;
import judge.httpclient.SimpleNameValueEntityFactory;
import judge.remote.RemoteOjInfo;
import judge.remote.account.RemoteAccount;
import judge.remote.submitter.CanonicalSubmitter;
import judge.remote.submitter.SubmissionInfo;

import org.apache.http.HttpEntity;
import org.springframework.stereotype.Component;

@Component
public class CSUSubmitter extends CanonicalSubmitter {

    @Override
    public RemoteOjInfo getOjInfo() {
        return CSUInfo.INFO;
    }

    @Override
    protected boolean needLogin() {
        return true;
    }

    @Override
    protected Integer getMaxRunId(SubmissionInfo info, DedicatedHttpClient client, boolean submitted) {
        String html = client.get("/OnlineJudge/status.php?problem_id=" + info.remoteProblemId + "&user_id=" + info.remoteAccountId).getBody();
        Matcher matcher = Pattern.compile("class='evenrow'><td>\\s*(\\d+)").matcher(html);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
    }

    @Override
    protected String submitCode(SubmissionInfo info, RemoteAccount remoteAccount, DedicatedHttpClient client) {
        HttpEntity entity = SimpleNameValueEntityFactory.create(
            "language", info.remotelanguage, //
            "id", info.remoteProblemId, //
            "source", info.sourceCode
        );
        client.post("/OnlineJudge/submit.php", entity, HttpStatusValidator.SC_MOVED_TEMPORARILY);
        return null;
    }

}
